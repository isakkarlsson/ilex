package interpreter.plog;

import java.util.List;

import parser.tree.Node;
import parser.tree.plog.ArrayNode;
import parser.tree.plog.AssignNode;
import parser.tree.plog.AttrNode;
import parser.tree.plog.CallNode;
import parser.tree.plog.CompNode;
import parser.tree.plog.ExprListNode;
import parser.tree.plog.ExprNode;
import parser.tree.plog.IfNode;
import parser.tree.plog.LookupVarNode;
import parser.tree.plog.NumNode;
import parser.tree.plog.ReadNode;
import parser.tree.plog.StmtListNode;
import parser.tree.plog.StringNode;
import parser.tree.plog.VarNode;
import parser.tree.plog.WhileNode;
import parser.tree.plog.WriteNode;

public abstract class Visitor {

	public Object visit(Node node) {
		if (node != null)
			return node.visit(this);
		return null;
	}

	public void visit(List<? extends Node> v) {
		for (Node n : v)
			visit(n);
	}

	public abstract Object visitComp(CompNode n);

	public abstract Object visitExpr(ExprNode n);

	public abstract Object visitAssign(AssignNode n);

	public abstract Object visitNum(NumNode n);

	public abstract Object visitVar(VarNode n);

	public abstract Object visitStmtList(StmtListNode n);

	public abstract Object visitWhile(WhileNode n);

	public abstract Object visitRead(ReadNode n);

	public abstract Object visitWrite(WriteNode n);

	public abstract Object visitIf(IfNode n);

	public abstract Object visitLookupVar(LookupVarNode n);

	public abstract Object visitString(StringNode n);

	public abstract Object visitCall(CallNode callNode);

	public abstract Object visitExprList(ExprListNode exprListNode);

	public abstract Object visitAttr(AttrNode attrNode);

	public abstract Object visitArray(ArrayNode arrayNode);
}
