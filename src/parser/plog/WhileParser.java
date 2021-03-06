package parser.plog;

import java.io.IOException;

import parser.Parser;
import parser.tree.Node;
import parser.tree.plog.WhileNode;
import token.Token;
import token.plog.ErrorCode;

public class WhileParser extends Parser {

	public WhileParser(Parser parent) {
		super(parent);
	}

	@Override
	public Node parse(Token token) throws IOException {
		WhileNode node = null;
		token = tokenizer().next(); // consume while

		CompareParser compParser = new CompareParser(this);
		Node compNode = compParser.parse(token);

		token = tokenizer().current();
		if (compNode != null) {
			node = new WhileNode(startLine());
			node.compare(compNode);

			token = tokenizer().current();
			StatementListParser slp = new StatementListParser(this);
			Node statementListNode = slp.parse(token);

			if (statementListNode != null) {
				node.statementList(statementListNode);
			} else {
				error(ErrorCode.WHILE_NO_BODY);
			}

		} else {
			error(ErrorCode.EXPECTED_COMPARE);
		}

		return node;
	}
}
