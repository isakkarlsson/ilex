package parser.tree.plog;

import runtime.plog.PString;
import interpreter.plog.Visitor;

public class StringNode extends TermNode {

	private PString string;

	public StringNode(int line, String string) {
		super(line);
		this.string = new PString(string);
	}

	public PString string() {
		return string;
	}

	@Override
	public Object visit(Visitor visitor) {
		return visitor.visitString(this);
	}

}
