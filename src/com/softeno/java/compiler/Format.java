package com.softeno.java.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softeno.java.compiler.Token.SpecialToken;

public class Format {
	
	public static final String PREPROCESSING = "*PREPROCESSING";
	private Map<String, Vector<Vector<PatternComponent>>> definitions = new HashMap<String, Vector<Vector<PatternComponent>>>();
	private Vector<Token> tokens = new Vector<Token>();
	
	private Format() {

	}

	public static Format parseFormatFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		char[] buff = new char[(int) file.length()];
		if (Compiler.verbose())
			System.out.println("Reading format file...");
		fr.read(buff);
		fr.close();
		String contents = new String(buff);
		contents = preprocess(contents);
		
		Format format = new Format();
		// Splitting into separate lines
		String[] statements = contents.split("\n");
		for (int i = 0; i < statements.length; i++) {
			if (statements[i].isEmpty())
				continue;
			String[] split = split(statements[i], ":=");
			String name = split[0];
			String[] statement = split(split[1], "|");
			for (int j = 0; j < statement.length; j++) {
				// Split into the individual parts of the definition
				String[] parts = split(statement[j].trim(), "+");
				Vector<String> partproc = new Vector<String>();
				boolean multiple = false;
				for (int k = 0; k < parts.length; k++) {
					String spart = parts[k].trim();
					if (spart.startsWith("(") && !spart.equals("(*)") && !spart.equals("(**)")) {
						multiple = true;
						// All combinations syntax
						String[] comp = spart.split("\\)\\(");
						String[] cparts = split(comp[0].substring(1), "+");
						String[] params = split(comp[1].substring(0, comp[1].length()-1), ",");
						int stars = 0;
						for (String cpart:cparts)
							if (cpart.equals("*")) 
								stars++;
						int combs = (int) Math.pow(params.length, stars);
						Vector<Vector<String>> combinations = new Vector<Vector<String>>(combs);
						for (int l = 0; l < combs; l++)
							combinations.add(new Vector<String>(cparts.length));
						Vector<Vector<String>> toRemove = new Vector<Vector<String>>();
						// Creating the combinations
						for (int q = 1, l = 0; l < cparts.length; l++) {
							String cpart = cparts[l].trim();
							if (cpart.equals("*")) {
								for (int m = 0; m < combs; m++) {
									Vector<String> current = combinations.get(m);
									// Make sure to get all combinations
									int in = (int) (m/(combs/(int) Math.pow(params.length, q))) % params.length;
									String param = params[in];
									// Avoid the loop case
									if (!(param.equals(name) && l == 0)) {
										current.add(param);
									} else {
										toRemove.add(current);
									}
								}
								q++;
							} else {
								for (int m = 0; m < combs; m++) {
									Vector<String> current = combinations.get(m);
									current.add(cpart);
								}
							}
						}
						// Remove the discarded combinations
						for (Vector<String> c:toRemove) {
							combinations.remove(c);
						}
						// Register the definitions
						for (Vector<String> com:combinations) {
							format.addDefinition(name, com, j);
						}
						// We don't allow any more parts before and after this part since it's
						// already covered inside the functionality.
						break;
					} else {
						partproc.add(spart);
					}
				}
				if (!multiple)
					format.addDefinition(name, partproc, j);
			}
		}

		format.optimizeTokens();
		return format;

	}

	private void addDefinition(String name, Vector<String> partproc, int depth) {
		if (!definitions.containsKey(name)) {
			definitions.put(name, new Vector<Vector<PatternComponent>>());
		}
		Pattern charcterRange = Pattern.compile("([a-zA-Z])-([a-zA-Z])");
		
		Vector<PatternComponent> components = new Vector<PatternComponent>(partproc.size());
		Vector<Token> newTokens = new Vector<Token>();
		for (int i = 0; i < partproc.size(); i++) {
			String part = partproc.get(i);
			Matcher matcher = charcterRange.matcher(part);
			if (part.startsWith("\'")) {
				Token token = new Token(part.substring(1, part.length()-1));
				if (!name.startsWith("*"))
					newTokens.add(token);
				components.add(token);
			} else if (part.equals("(*)")) {
				// TODO This should be as described in the format file
				components.add(SpecialToken.STRING_LITTERAL);
			} else if (part.equals("(**)")) {
				components.add(SpecialToken.STRING_LITTERAL);
				
			} else if (matcher.find()) {
				String start = matcher.group(1);
				String end = matcher.group(2);
				components.add(new CharacterRange(start, end));
			} else {
				// TODO reference limitations must be implemented
				PatternReference reference = new PatternReference(part, depth);
				components.add(reference);
			}
		}
		tokens.addAll(newTokens);
		definitions.get(name).add(components);
		
	}
	
	private void optimizeTokens() {
		Vector<Token> optimal = new Vector<Token>();
		optim : for (Token token: tokens) {
			if (!optimal.isEmpty())
			for (Token t:optimal) {
				if (token.equals(t))
					continue optim;
			}
			optimal.add(token);
		}
		tokens = optimal;
	}

	private static String[] split(String str, String splitter) {
		char[] breaks = {
				'\'', '('
		};
		char[] unbreaks = {
				'\'', ')'	
		};
		Vector<Character> expect = new Vector<Character>();
		Vector<String> split = new Vector<String>();
		int last = 0;
		boolean cleartext = false;
		for (int j = 0, i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// Stop capture when:
			int p = contains(breaks, String.valueOf(c));
			int p2 = contains(unbreaks, String.valueOf(c));
			if (p != -1 || p2 != -1) {
				if (!expect.isEmpty() && expect.lastElement().equals(c)) {
					expect.remove(expect.size()-1);
					cleartext = false;
				} else if (!cleartext) {
					expect.add(unbreaks[p != -1 ? p : p2]);
					cleartext = c == '\'';
				}
			} else if (expect.isEmpty() && c == splitter.charAt(j)) {
				if (j >= splitter.length()-1) {
					// match found
					split.add(str.substring(last, i-(splitter.length()-1)));
					last = i+1;
				} else {
					j++;
				}
			}
		}
		split.add(str.substring(last));
		if (!expect.isEmpty())
			throw new SyntaxError("Unclosed break i EBNF");
		String[] stri = new String[split.size()];
		for (int i = 0; i < stri.length; i++)
			stri[i] = split.get(i).trim();
		return stri;
	}

	private static int contains(char[] breaks, String s) {
		for (int i = 0; i < s.length(); i++) {
			for (int j = 0; j < breaks.length; j++) {
				if (s.charAt(i) == breaks[j])
					return j;
			}
		}
		return -1;
	}

	private static String preprocess(String contents) {
		String[] lines = contents.split("[\n\r]+");
		StringBuffer ready = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			String l = lines[i];
			if (!l.startsWith("#") || l.matches("^\\s*$")) {
				ready.append(l);
				if (!l.trim().endsWith("|")) {
					ready.append('\n');
				}
			}
		}
		return ready.toString();
	}

	public void printDefinitons() {
		for (String key:definitions.keySet()) {
			System.out.println(key+":");
			for (Vector<PatternComponent> list:definitions.get(key)) {
				for (int i = 0; i < list.size(); i++) {
					System.out.println("\t"+list.get(i));
				}
			}
		}
	}
	
	public Token[] getTokens() {
		Token[] t = new Token[tokens.size()];
		t = tokens.toArray(t); 
		return t;
	}

	public Vector<Vector<PatternComponent>> getPreprocess() {
		return definitions.get(PREPROCESSING);
	}
}
