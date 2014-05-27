package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class StringTypeDenoter extends TypeDenoter {

	public IntegerLiteral IL;

	public StringTypeDenoter(SourcePosition thePosition) {
		super(thePosition);
		IL = new IntegerLiteral("0", thePosition);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ErrorTypeDenoter)
			return true;
		else
		return (obj != null && obj instanceof StringTypeDenoter);
	}

	@Override
	public Object visit(Visitor v, Object o) {
		return v.visitStringTypeDenoter(this, o);
	}

}
