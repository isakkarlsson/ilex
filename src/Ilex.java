import interpreter.Stack;
import interpreter.TableEntry;
import interpreter.TableKey;
import interpreter.plog.Interpreter;
import interpreter.plog.Visitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import message.MessageHandler;
import message.ParseAdapter;
import message.ParseListener;
import message.SourceListener;
import parser.BufferedSource;
import parser.Parser;
import parser.Source;
import parser.Tokenizer;
import parser.plog.PlogParser;
import parser.plog.PlogTokenizer;
import parser.tree.Node;
import runtime.plog.Builtin;
import runtime.plog.PModule;
import runtime.plog.PString;
import token.Token;

public class Ilex {

	private static String file = "";
	private static Map<Integer, String> source = new HashMap<Integer, String>();
	private static ParseListener errorListener = new ParseAdapter() {
		@Override
		public void error(int line, int pos, String text, String error,
				boolean fatal) {
			if (fatal) {
				MessageHandler.getInstance().parserSummary(0);
				System.exit(-1);
			}
			StringBuilder builder = new StringBuilder();
			builder.append(" File '" + file + "', line " + line + "\n");
			builder.append(" " + source.get(line) + "\n");
			for (int n = 0; n < pos + 1; n++) {
				builder.append(' ');
			}
			builder.append("^\nSyntax Error: ");
			builder.append(error);
			System.out.println(builder.toString());
		}
	};

	private static ParseListener parseSummary = new ParseAdapter() {

		@Override
		public void parserSummary(long took, int errors) {
			System.out.format("\n\t*******************************\n"
					+ "\t   %d ms total parsing time  \n"
					+ "\t   %d syntax errors\n\n", took, errors);
		}
	};

	private static SourceListener sourceListener = new SourceListener() {

		@Override
		public void line(int num, String line) {
			source.put(num, line);
		}
	};

	private static ParseListener parseDebug = new ParseAdapter() {

		@Override
		public void token(Token token) {
			System.out
					.format(">>> %-15s \t [line=%03d, pos=%d, text='%s', value='%s']\n",
							token.type(), token.line(), token.position(),
							token.text(), token.value());
		}
	};

	public static void main(String[] args) {
		try {
//			 args = new String[] { "proto.ilex" };

			MessageHandler.getInstance().addParseListener(errorListener);
			MessageHandler.getInstance().addSourceListener(sourceListener);

			List<String> arguments = Arrays.asList(args);
			if (arguments.contains("--summary")) {
				MessageHandler.getInstance().addParseListener(parseSummary);
			} else if (arguments.contains("--debug")) {
				MessageHandler.getInstance().addParseListener(parseDebug);
			}

			PModule mod = new PModule(file);

			mod.dict("string", Builtin.string);
			mod.dict("object", Builtin.object);
			mod.dict("true", Builtin.ptrue);
			mod.dict("false", Builtin.pfalse);
			mod.dict("system", Builtin.system);
			mod.dict("test", Builtin.test);
			mod.dict("pfunc", Builtin.pfunc);
			mod.dict("array", Builtin.parray);

			if (arguments.size() > 0) {
				file = arguments.get(0);
				mod.dict("__file__", new PString(file));
				Source source = new BufferedSource(new File(file));
				Tokenizer tokenizer = new PlogTokenizer(source);

				Parser parser = new PlogParser(tokenizer, true);
				Node root = parser.parse();
				if (!parser.errors()) {
					Visitor interpreter = new Interpreter(mod);
					interpreter.visit(root);
				}
			} else {
				file = "<stdin>";
				Source source = null;
				Tokenizer tokenizer = null;

				Parser parser = null;
				Scanner sc = new Scanner(System.in);
				mod.dict("__file__", new PString(file));
				while (true) {
					System.out.print(">> ");
					String code = sc.nextLine();

					source = new BufferedSource(new BufferedReader(
							new StringReader(code)));
					tokenizer = new PlogTokenizer(source);
					parser = new PlogParser(tokenizer);
					if (code.trim().length() == 0)
						continue;
					Node root = parser.parse();
					if (!parser.errors()) {
						try {
							Visitor interpreter = new Interpreter(mod);
							Object value = interpreter.visit(root);
							if (value != null) {
								System.out.println(value);
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}

					MessageHandler.getInstance().reset();
				}
			}

			if (arguments.contains("--stack")) {
				System.out.println("\t************ Stack ************");
				System.out.println("\tLevel: "
						+ Stack.getInstance().currentNestingLevel());
				for (TableEntry entry : Stack.getInstance().local().sorted()) {
					System.out.println("\t   " + entry.name() + " "
							+ entry.getAttribute(TableKey.CONSTANT));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
