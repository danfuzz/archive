// $ANTLR 2.7.2a2 (20020112-1): "stu.g" -> "StuParser.java"$

    package com.milk.stu.parser;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class StuParser extends antlr.LLkParser
       implements StuTokenTypes
 {

protected StuParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public StuParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected StuParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public StuParser(TokenStream lexer) {
  this(lexer,2);
}

public StuParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void parseFile() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parseFile_AST = null;
		
		try {      // for error handling
			statementList();
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			parseFile_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = parseFile_AST;
	}
	
	public final void statementList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statementList_AST = null;
		
		try {      // for error handling
			statement();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop5:
			do {
				if ((LA(1)==SEMI)) {
					match(SEMI);
					statement();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop5;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				statementList_AST = (AST)currentAST.root;
				statementList_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(BLOCK_EXPR,"BLOCK_EXPR")).add(statementList_AST));
				currentAST.root = statementList_AST;
				currentAST.child = statementList_AST!=null &&statementList_AST.getFirstChild()!=null ?
					statementList_AST.getFirstChild() : statementList_AST;
				currentAST.advanceChildToEnd();
			}
			statementList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = statementList_AST;
	}
	
	public final void blockExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockExpression_AST = null;
		
		try {      // for error handling
			match(OCURLY);
			statementList();
			astFactory.addASTChild(currentAST, returnAST);
			match(CCURLY);
			blockExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = blockExpression_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8 = false;
			if (((LA(1)==EOF||LA(1)==CCURLY||LA(1)==SEMI))) {
				int _m8 = mark();
				synPredMatched8 = true;
				inputState.guessing++;
				try {
					{
					switch ( LA(1)) {
					case EOF:
					{
						match(Token.EOF_TYPE);
						break;
					}
					case CCURLY:
					{
						match(CCURLY);
						break;
					}
					case SEMI:
					{
						match(SEMI);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8 = false;
				}
				rewind(_m8);
				inputState.guessing--;
			}
			if ( synPredMatched8 ) {
				statement_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched11 = false;
				if (((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2))))) {
					int _m11 = mark();
					synPredMatched11 = true;
					inputState.guessing++;
					try {
						{
						{
						match(_tokenSet_5);
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched11 = false;
					}
					rewind(_m11);
					inputState.guessing--;
				}
				if ( synPredMatched11 ) {
					expression();
					astFactory.addASTChild(currentAST, returnAST);
					statement_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched13 = false;
					if (((LA(1)==IF) && (LA(2)==OPAREN))) {
						int _m13 = mark();
						synPredMatched13 = true;
						inputState.guessing++;
						try {
							{
							match(IF);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched13 = false;
						}
						rewind(_m13);
						inputState.guessing--;
					}
					if ( synPredMatched13 ) {
						ifExpression();
						astFactory.addASTChild(currentAST, returnAST);
						statement();
						astFactory.addASTChild(currentAST, returnAST);
						statement_AST = (AST)currentAST.root;
					}
					else {
						boolean synPredMatched15 = false;
						if (((LA(1)==LOOP) && (LA(2)==OCURLY||LA(2)==IDENTIFIER))) {
							int _m15 = mark();
							synPredMatched15 = true;
							inputState.guessing++;
							try {
								{
								match(LOOP);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched15 = false;
							}
							rewind(_m15);
							inputState.guessing--;
						}
						if ( synPredMatched15 ) {
							loopExpression();
							astFactory.addASTChild(currentAST, returnAST);
							statement();
							astFactory.addASTChild(currentAST, returnAST);
							statement_AST = (AST)currentAST.root;
						}
						else {
							boolean synPredMatched17 = false;
							if (((LA(1)==OCURLY) && (_tokenSet_6.member(LA(2))))) {
								int _m17 = mark();
								synPredMatched17 = true;
								inputState.guessing++;
								try {
									{
									match(OCURLY);
									}
								}
								catch (RecognitionException pe) {
									synPredMatched17 = false;
								}
								rewind(_m17);
								inputState.guessing--;
							}
							if ( synPredMatched17 ) {
								blockExpression();
								astFactory.addASTChild(currentAST, returnAST);
								statement();
								astFactory.addASTChild(currentAST, returnAST);
								statement_AST = (AST)currentAST.root;
							}
							else {
								boolean synPredMatched19 = false;
								if (((LA(1)==DEF) && (_tokenSet_7.member(LA(2))))) {
									int _m19 = mark();
									synPredMatched19 = true;
									inputState.guessing++;
									try {
										{
										match(DEF);
										defIdentifier();
										match(ASSIGN);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched19 = false;
									}
									rewind(_m19);
									inputState.guessing--;
								}
								if ( synPredMatched19 ) {
									defVariable();
									astFactory.addASTChild(currentAST, returnAST);
									statement_AST = (AST)currentAST.root;
								}
								else {
									boolean synPredMatched21 = false;
									if (((LA(1)==DEF) && (_tokenSet_7.member(LA(2))))) {
										int _m21 = mark();
										synPredMatched21 = true;
										inputState.guessing++;
										try {
											{
											match(DEF);
											defIdentifier();
											match(OPAREN);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched21 = false;
										}
										rewind(_m21);
										inputState.guessing--;
									}
									if ( synPredMatched21 ) {
										defFunction();
										astFactory.addASTChild(currentAST, returnAST);
										statement();
										astFactory.addASTChild(currentAST, returnAST);
										statement_AST = (AST)currentAST.root;
									}
									else {
										boolean synPredMatched24 = false;
										if (((LA(1)==DEF) && (_tokenSet_7.member(LA(2))))) {
											int _m24 = mark();
											synPredMatched24 = true;
											inputState.guessing++;
											try {
												{
												match(DEF);
												defIdentifier();
												{
												switch ( LA(1)) {
												case EOF:
												{
													match(Token.EOF_TYPE);
													break;
												}
												case CCURLY:
												{
													match(CCURLY);
													break;
												}
												case SEMI:
												{
													match(SEMI);
													break;
												}
												default:
												{
													throw new NoViableAltException(LT(1), getFilename());
												}
												}
												}
												}
											}
											catch (RecognitionException pe) {
												synPredMatched24 = false;
											}
											rewind(_m24);
											inputState.guessing--;
										}
										if ( synPredMatched24 ) {
											defVariableNull();
											astFactory.addASTChild(currentAST, returnAST);
											statement_AST = (AST)currentAST.root;
										}
										else {
											throw new NoViableAltException(LT(1), getFilename());
										}
										}}}}}}}
									}
									catch (RecognitionException ex) {
										if (inputState.guessing==0) {
											reportError(ex);
											consume();
											consumeUntil(_tokenSet_8);
										} else {
										  throw ex;
										}
									}
									returnAST = statement_AST;
								}
								
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			assignExpression();
			astFactory.addASTChild(currentAST, returnAST);
			expression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = expression_AST;
	}
	
	public final void ifExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifExpression_AST = null;
		
		try {      // for error handling
			AST tmp5_AST = null;
			tmp5_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp5_AST);
			match(IF);
			parenExpression();
			astFactory.addASTChild(currentAST, returnAST);
			blockExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ELSE:
			{
				elseClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case OCURLY:
			case CCURLY:
			case SEMI:
			case IF:
			case LOOP:
			case DEF:
			case ASSIGN:
			case OPAREN:
			case ASSIGN_MATCH:
			case BOOL_OR:
			case BOOL_AND:
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case NOT:
			case INVERT:
			case DOT:
			case IDENTIFIER:
			case CPAREN:
			case OSQUARE:
			case CSQUARE:
			case TRUE:
			case FALSE:
			case INFINITY:
			case NAN:
			case NULL:
			case LITERAL_INTEGER:
			case LITERAL_FLOAT:
			case LITERAL_STRING:
			case LITERAL_URI:
			case QUASILITERAL_STRING:
			case COLON:
			case GET:
			case RETURN:
			case BREAK:
			case CONTINUE:
			case COMMA:
			case FN:
			case FNAME:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			ifExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = ifExpression_AST;
	}
	
	public final void loopExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST loopExpression_AST = null;
		
		try {      // for error handling
			AST tmp6_AST = null;
			tmp6_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp6_AST);
			match(LOOP);
			{
			switch ( LA(1)) {
			case IDENTIFIER:
			{
				AST tmp7_AST = null;
				tmp7_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp7_AST);
				match(IDENTIFIER);
				break;
			}
			case OCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			blockExpression();
			astFactory.addASTChild(currentAST, returnAST);
			loopExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = loopExpression_AST;
	}
	
	public final void defIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defIdentifier_AST = null;
		AST si_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENTIFIER:
			{
				AST tmp8_AST = null;
				tmp8_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp8_AST);
				match(IDENTIFIER);
				defIdentifier_AST = (AST)currentAST.root;
				break;
			}
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case NOT:
			case INVERT:
			case LITERAL_STRING:
			case LITERAL_URI:
			case COLON:
			case GET:
			case XML_ENTITY:
			{
				specialIdentifier();
				si_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					defIdentifier_AST = (AST)currentAST.root;
					defIdentifier_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(FNAME,"FNAME")).add(si_AST));
					currentAST.root = defIdentifier_AST;
					currentAST.child = defIdentifier_AST!=null &&defIdentifier_AST.getFirstChild()!=null ?
						defIdentifier_AST.getFirstChild() : defIdentifier_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = defIdentifier_AST;
	}
	
	public final void defVariable() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defVariable_AST = null;
		
		try {      // for error handling
			AST tmp9_AST = null;
			tmp9_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp9_AST);
			match(DEF);
			defIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			match(ASSIGN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			defVariable_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = defVariable_AST;
	}
	
	public final void defFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defFunction_AST = null;
		AST id_AST = null;
		AST pl_AST = null;
		AST bexp_AST = null;
		
		try {      // for error handling
			AST tmp11_AST = null;
			tmp11_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp11_AST);
			match(DEF);
			defIdentifier();
			id_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			paramList();
			pl_AST = (AST)returnAST;
			blockExpression();
			bexp_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				defFunction_AST = (AST)currentAST.root;
				AST fn = (AST)astFactory.make( (new ASTArray(4)).add((AST)astFactory.create(FN,"FN")).add(astFactory.dupTree(id_AST)).add(pl_AST).add(bexp_AST));
				defFunction_AST.addChild (fn);
				
			}
			defFunction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = defFunction_AST;
	}
	
	public final void defVariableNull() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defVariableNull_AST = null;
		
		try {      // for error handling
			AST tmp12_AST = null;
			tmp12_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp12_AST);
			match(DEF);
			defIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				defVariableNull_AST = (AST)currentAST.root;
				defVariableNull_AST.addChild ((AST)astFactory.create(NULL,"NULL"));
			}
			defVariableNull_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = defVariableNull_AST;
	}
	
	public final void paramList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST paramList_AST = null;
		Token  opar = null;
		AST opar_AST = null;
		
		try {      // for error handling
			opar = LT(1);
			opar_AST = (AST)astFactory.create(opar);
			astFactory.makeASTRoot(currentAST, opar_AST);
			match(OPAREN);
			{
			switch ( LA(1)) {
			case IDENTIFIER:
			{
				AST tmp13_AST = null;
				tmp13_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp13_AST);
				match(IDENTIFIER);
				{
				_loop96:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						AST tmp15_AST = null;
						tmp15_AST = (AST)astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp15_AST);
						match(IDENTIFIER);
					}
					else {
						break _loop96;
					}
					
				} while (true);
				}
				break;
			}
			case CPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(CPAREN);
			if ( inputState.guessing==0 ) {
				opar_AST.setType (PARAM_LIST);
			}
			paramList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = paramList_AST;
	}
	
	public final void assignExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignExpression_AST = null;
		
		try {      // for error handling
			orExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ASSIGN:
			case ASSIGN_MATCH:
			{
				{
				switch ( LA(1)) {
				case ASSIGN:
				{
					AST tmp17_AST = null;
					tmp17_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp17_AST);
					match(ASSIGN);
					break;
				}
				case ASSIGN_MATCH:
				{
					AST tmp18_AST = null;
					tmp18_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp18_AST);
					match(ASSIGN_MATCH);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				assignExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case CCURLY:
			case SEMI:
			case CPAREN:
			case CSQUARE:
			case COLON:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			assignExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = assignExpression_AST;
	}
	
	public final void orExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpression_AST = null;
		
		try {      // for error handling
			andExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop34:
			do {
				if ((LA(1)==BOOL_OR)) {
					AST tmp19_AST = null;
					tmp19_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp19_AST);
					match(BOOL_OR);
					andExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop34;
				}
				
			} while (true);
			}
			orExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = orExpression_AST;
	}
	
	public final void andExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpression_AST = null;
		
		try {      // for error handling
			equalityExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop37:
			do {
				if ((LA(1)==BOOL_AND)) {
					AST tmp20_AST = null;
					tmp20_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp20_AST);
					match(BOOL_AND);
					equalityExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop37;
				}
				
			} while (true);
			}
			andExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = andExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		
		try {      // for error handling
			relationalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop41:
			do {
				if ((LA(1)==EQ||LA(1)==NE)) {
					{
					switch ( LA(1)) {
					case EQ:
					{
						AST tmp21_AST = null;
						tmp21_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp21_AST);
						match(EQ);
						break;
					}
					case NE:
					{
						AST tmp22_AST = null;
						tmp22_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp22_AST);
						match(NE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					relationalExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop41;
				}
				
			} while (true);
			}
			equalityExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_16);
			} else {
			  throw ex;
			}
		}
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		
		try {      // for error handling
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop45:
			do {
				if (((LA(1) >= LT && LA(1) <= GE))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						AST tmp23_AST = null;
						tmp23_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp23_AST);
						match(LT);
						break;
					}
					case GT:
					{
						AST tmp24_AST = null;
						tmp24_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp24_AST);
						match(GT);
						break;
					}
					case LE:
					{
						AST tmp25_AST = null;
						tmp25_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp25_AST);
						match(LE);
						break;
					}
					case GE:
					{
						AST tmp26_AST = null;
						tmp26_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp26_AST);
						match(GE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					logicalExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop45;
				}
				
			} while (true);
			}
			relationalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = relationalExpression_AST;
	}
	
	public final void logicalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;
		
		try {      // for error handling
			shiftExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop49:
			do {
				if (((LA(1) >= AND && LA(1) <= XOR))) {
					{
					switch ( LA(1)) {
					case AND:
					{
						AST tmp27_AST = null;
						tmp27_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp27_AST);
						match(AND);
						break;
					}
					case OR:
					{
						AST tmp28_AST = null;
						tmp28_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp28_AST);
						match(OR);
						break;
					}
					case XOR:
					{
						AST tmp29_AST = null;
						tmp29_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp29_AST);
						match(XOR);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					shiftExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop49;
				}
				
			} while (true);
			}
			logicalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_18);
			} else {
			  throw ex;
			}
		}
		returnAST = logicalExpression_AST;
	}
	
	public final void shiftExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shiftExpression_AST = null;
		
		try {      // for error handling
			additiveExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop53:
			do {
				if ((LA(1)==LSHIFT||LA(1)==RSHIFT)) {
					{
					switch ( LA(1)) {
					case LSHIFT:
					{
						AST tmp30_AST = null;
						tmp30_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp30_AST);
						match(LSHIFT);
						break;
					}
					case RSHIFT:
					{
						AST tmp31_AST = null;
						tmp31_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp31_AST);
						match(RSHIFT);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					additiveExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop53;
				}
				
			} while (true);
			}
			shiftExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = shiftExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		try {      // for error handling
			multiplicativeExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop57:
			do {
				if ((LA(1)==ADD||LA(1)==SUB)) {
					{
					switch ( LA(1)) {
					case ADD:
					{
						AST tmp32_AST = null;
						tmp32_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp32_AST);
						match(ADD);
						break;
					}
					case SUB:
					{
						AST tmp33_AST = null;
						tmp33_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp33_AST);
						match(SUB);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					multiplicativeExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop57;
				}
				
			} while (true);
			}
			additiveExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_20);
			} else {
			  throw ex;
			}
		}
		returnAST = additiveExpression_AST;
	}
	
	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicativeExpression_AST = null;
		
		try {      // for error handling
			powerExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop61:
			do {
				if (((LA(1) >= MUL && LA(1) <= MOD))) {
					{
					switch ( LA(1)) {
					case MUL:
					{
						AST tmp34_AST = null;
						tmp34_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp34_AST);
						match(MUL);
						break;
					}
					case DIV:
					{
						AST tmp35_AST = null;
						tmp35_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp35_AST);
						match(DIV);
						break;
					}
					case REMAINDER:
					{
						AST tmp36_AST = null;
						tmp36_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp36_AST);
						match(REMAINDER);
						break;
					}
					case MOD:
					{
						AST tmp37_AST = null;
						tmp37_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp37_AST);
						match(MOD);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					powerExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop61;
				}
				
			} while (true);
			}
			multiplicativeExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_21);
			} else {
			  throw ex;
			}
		}
		returnAST = multiplicativeExpression_AST;
	}
	
	public final void powerExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST powerExpression_AST = null;
		
		try {      // for error handling
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop64:
			do {
				if ((LA(1)==POW)) {
					AST tmp38_AST = null;
					tmp38_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp38_AST);
					match(POW);
					unaryExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop64;
				}
				
			} while (true);
			}
			powerExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = powerExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ADD:
			case SUB:
			case NOT:
			case INVERT:
			{
				{
				switch ( LA(1)) {
				case ADD:
				{
					AST tmp39_AST = null;
					tmp39_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp39_AST);
					match(ADD);
					break;
				}
				case SUB:
				{
					AST tmp40_AST = null;
					tmp40_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp40_AST);
					match(SUB);
					break;
				}
				case NOT:
				{
					AST tmp41_AST = null;
					tmp41_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp41_AST);
					match(NOT);
					break;
				}
				case INVERT:
				{
					AST tmp42_AST = null;
					tmp42_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp42_AST);
					match(INVERT);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case OCURLY:
			case IF:
			case LOOP:
			case OPAREN:
			case IDENTIFIER:
			case OSQUARE:
			case TRUE:
			case FALSE:
			case INFINITY:
			case NAN:
			case NULL:
			case LITERAL_INTEGER:
			case LITERAL_FLOAT:
			case LITERAL_STRING:
			case LITERAL_URI:
			case QUASILITERAL_STRING:
			case GET:
			case RETURN:
			case BREAK:
			case CONTINUE:
			case FN:
			case FNAME:
			{
				postfixExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_23);
			} else {
			  throw ex;
			}
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void postfixExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixExpression_AST = null;
		Token  dot = null;
		AST dot_AST = null;
		Token  opar = null;
		AST opar_AST = null;
		Token  osq = null;
		AST osq_AST = null;
		
		try {      // for error handling
			simpleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop70:
			do {
				switch ( LA(1)) {
				case DOT:
				{
					dot = LT(1);
					dot_AST = (AST)astFactory.create(dot);
					astFactory.makeASTRoot(currentAST, dot_AST);
					match(DOT);
					AST tmp43_AST = null;
					tmp43_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp43_AST);
					match(IDENTIFIER);
					{
					if ((LA(1)==OPAREN) && (_tokenSet_24.member(LA(2)))) {
						match(OPAREN);
						argList();
						astFactory.addASTChild(currentAST, returnAST);
						match(CPAREN);
						if ( inputState.guessing==0 ) {
							dot_AST.setType(APPLY_METH);
						}
					}
					else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_2.member(LA(2)))) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					break;
				}
				case OPAREN:
				{
					opar = LT(1);
					opar_AST = (AST)astFactory.create(opar);
					astFactory.makeASTRoot(currentAST, opar_AST);
					match(OPAREN);
					if ( inputState.guessing==0 ) {
						opar_AST.setType(APPLY_FUNC); opar_AST.setText ("APPLY_FUNC");
					}
					argList();
					astFactory.addASTChild(currentAST, returnAST);
					match(CPAREN);
					break;
				}
				case OSQUARE:
				{
					osq = LT(1);
					osq_AST = (AST)astFactory.create(osq);
					astFactory.makeASTRoot(currentAST, osq_AST);
					match(OSQUARE);
					if ( inputState.guessing==0 ) {
						osq_AST.setType(AREF); osq_AST.setText ("AREF");
					}
					argList();
					astFactory.addASTChild(currentAST, returnAST);
					match(CSQUARE);
					break;
				}
				default:
				{
					break _loop70;
				}
				}
			} while (true);
			}
			postfixExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_23);
			} else {
			  throw ex;
			}
		}
		returnAST = postfixExpression_AST;
	}
	
	public final void simpleExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleExpression_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  quasi = null;
		AST quasi_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TRUE:
			{
				AST tmp48_AST = null;
				tmp48_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp48_AST);
				match(TRUE);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp49_AST = null;
				tmp49_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp49_AST);
				match(FALSE);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case INFINITY:
			{
				AST tmp50_AST = null;
				tmp50_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp50_AST);
				match(INFINITY);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case NAN:
			{
				AST tmp51_AST = null;
				tmp51_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp51_AST);
				match(NAN);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case NULL:
			{
				AST tmp52_AST = null;
				tmp52_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp52_AST);
				match(NULL);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_INTEGER:
			{
				AST tmp53_AST = null;
				tmp53_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp53_AST);
				match(LITERAL_INTEGER);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_FLOAT:
			{
				AST tmp54_AST = null;
				tmp54_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp54_AST);
				match(LITERAL_FLOAT);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_STRING:
			{
				AST tmp55_AST = null;
				tmp55_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp55_AST);
				match(LITERAL_STRING);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_URI:
			{
				AST tmp56_AST = null;
				tmp56_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp56_AST);
				match(LITERAL_URI);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case QUASILITERAL_STRING:
			{
				AST tmp57_AST = null;
				tmp57_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp57_AST);
				match(QUASILITERAL_STRING);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case FNAME:
			{
				specialFunctionName();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case OPAREN:
			{
				parenExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case OSQUARE:
			case GET:
			{
				collectionExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case BREAK:
			{
				breakExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case CONTINUE:
			{
				continueExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case RETURN:
			{
				returnExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case FN:
			{
				anonymousFunction();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case IF:
			{
				ifExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case LOOP:
			{
				loopExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			case OCURLY:
			{
				blockExpression();
				astFactory.addASTChild(currentAST, returnAST);
				simpleExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==IDENTIFIER) && (_tokenSet_25.member(LA(2)))) {
					AST tmp58_AST = null;
					tmp58_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp58_AST);
					match(IDENTIFIER);
					simpleExpression_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==IDENTIFIER) && (LA(2)==QUASILITERAL_STRING)) {
					id = LT(1);
					id_AST = (AST)astFactory.create(id);
					match(IDENTIFIER);
					quasi = LT(1);
					quasi_AST = (AST)astFactory.create(quasi);
					match(QUASILITERAL_STRING);
					if ( inputState.guessing==0 ) {
						simpleExpression_AST = (AST)currentAST.root;
						simpleExpression_AST = (AST)astFactory.make( (new ASTArray(3)).add((AST)astFactory.create(ID_QUASI,"ID_QUASI")).add(id_AST).add(quasi_AST));
						currentAST.root = simpleExpression_AST;
						currentAST.child = simpleExpression_AST!=null &&simpleExpression_AST.getFirstChild()!=null ?
							simpleExpression_AST.getFirstChild() : simpleExpression_AST;
						currentAST.advanceChildToEnd();
					}
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = simpleExpression_AST;
	}
	
	public final void argList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case OCURLY:
			case IF:
			case LOOP:
			case OPAREN:
			case ADD:
			case SUB:
			case NOT:
			case INVERT:
			case IDENTIFIER:
			case OSQUARE:
			case TRUE:
			case FALSE:
			case INFINITY:
			case NAN:
			case NULL:
			case LITERAL_INTEGER:
			case LITERAL_FLOAT:
			case LITERAL_STRING:
			case LITERAL_URI:
			case QUASILITERAL_STRING:
			case GET:
			case RETURN:
			case BREAK:
			case CONTINUE:
			case FN:
			case FNAME:
			{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop84:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						expression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop84;
					}
					
				} while (true);
				}
				break;
			}
			case CPAREN:
			case CSQUARE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			argList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_26);
			} else {
			  throw ex;
			}
		}
		returnAST = argList_AST;
	}
	
	public final void specialFunctionName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST specialFunctionName_AST = null;
		
		try {      // for error handling
			AST tmp60_AST = null;
			tmp60_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp60_AST);
			match(FNAME);
			specialIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			specialFunctionName_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = specialFunctionName_AST;
	}
	
	public final void parenExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parenExpression_AST = null;
		Token  opar = null;
		AST opar_AST = null;
		Token  id = null;
		AST id_AST = null;
		
		try {      // for error handling
			if ((LA(1)==OPAREN) && (_tokenSet_3.member(LA(2)))) {
				match(OPAREN);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(CPAREN);
				parenExpression_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==OPAREN) && (LA(2)==COLON)) {
				opar = LT(1);
				opar_AST = (AST)astFactory.create(opar);
				astFactory.makeASTRoot(currentAST, opar_AST);
				match(OPAREN);
				match(COLON);
				id = LT(1);
				id_AST = (AST)astFactory.create(id);
				astFactory.addASTChild(currentAST, id_AST);
				match(IDENTIFIER);
				match(COLON);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(CPAREN);
				if ( inputState.guessing==0 ) {
					opar_AST.setType (QUALIFIED_EXPR);
					opar_AST.setText ("QUALIFIED_EXPR");
					
				}
				parenExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = parenExpression_AST;
	}
	
	public final void collectionExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST collectionExpression_AST = null;
		Token  osq1 = null;
		AST osq1_AST = null;
		Token  osq2 = null;
		AST osq2_AST = null;
		
		try {      // for error handling
			if ((LA(1)==OSQUARE) && (_tokenSet_28.member(LA(2)))) {
				osq1 = LT(1);
				osq1_AST = (AST)astFactory.create(osq1);
				astFactory.makeASTRoot(currentAST, osq1_AST);
				match(OSQUARE);
				mapOrList();
				astFactory.addASTChild(currentAST, returnAST);
				match(CSQUARE);
				if ( inputState.guessing==0 ) {
					// determine if it's a map or a list, and complain if it's neither/both
					boolean isMap = false;
					boolean isList = false;
					AST child = osq1_AST.getFirstChild ();
					while (child != null)
					{
					if (child.getType () == COLON) isMap = true;
					else isList = true;
					child = child.getNextSibling ();
					}
					if (isMap)
					{
					if (isList)
					{
					throw new SemanticException ("malformed list/map");
					}
					osq1_AST.setType (MAKE_MAP); 
					osq1_AST.setText ("MAKE_MAP");
					}
					else
					{      
					osq1_AST.setType (MAKE_LIST); 
					osq1_AST.setText ("MAKE_LIST");
					}
					
				}
				collectionExpression_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==OSQUARE) && (LA(2)==COLON)) {
				osq2 = LT(1);
				osq2_AST = (AST)astFactory.create(osq2);
				astFactory.makeASTRoot(currentAST, osq2_AST);
				match(OSQUARE);
				match(COLON);
				match(CSQUARE);
				if ( inputState.guessing==0 ) {
					osq2_AST.setType (MAKE_MAP); 
					osq2_AST.setText ("MAKE_MAP");
				}
				collectionExpression_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==GET)) {
				AST tmp69_AST = null;
				tmp69_AST = (AST)astFactory.create(LT(1));
				match(GET);
				if ( inputState.guessing==0 ) {
					collectionExpression_AST = (AST)currentAST.root;
					collectionExpression_AST = (AST)astFactory.make( (new ASTArray(1)).add((AST)astFactory.create(MAKE_LIST,"MAKE_LIST")));
					currentAST.root = collectionExpression_AST;
					currentAST.child = collectionExpression_AST!=null &&collectionExpression_AST.getFirstChild()!=null ?
						collectionExpression_AST.getFirstChild() : collectionExpression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = collectionExpression_AST;
	}
	
	public final void breakExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST breakExpression_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST ex_AST = null;
		
		try {      // for error handling
			AST tmp70_AST = null;
			tmp70_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp70_AST);
			match(BREAK);
			{
			switch ( LA(1)) {
			case IDENTIFIER:
			{
				id = LT(1);
				id_AST = (AST)astFactory.create(id);
				astFactory.addASTChild(currentAST, id_AST);
				match(IDENTIFIER);
				break;
			}
			case EOF:
			case CCURLY:
			case SEMI:
			case ASSIGN:
			case OPAREN:
			case ASSIGN_MATCH:
			case BOOL_OR:
			case BOOL_AND:
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case DOT:
			case CPAREN:
			case OSQUARE:
			case CSQUARE:
			case COLON:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			if ((LA(1)==OPAREN) && (LA(2)==OPAREN)) {
				match(OPAREN);
				parenExpression();
				ex_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp72_AST = null;
				tmp72_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp72_AST);
				match(CPAREN);
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_2.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			breakExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = breakExpression_AST;
	}
	
	public final void continueExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continueExpression_AST = null;
		Token  id = null;
		AST id_AST = null;
		
		try {      // for error handling
			AST tmp73_AST = null;
			tmp73_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp73_AST);
			match(CONTINUE);
			{
			switch ( LA(1)) {
			case IDENTIFIER:
			{
				id = LT(1);
				id_AST = (AST)astFactory.create(id);
				astFactory.addASTChild(currentAST, id_AST);
				match(IDENTIFIER);
				break;
			}
			case EOF:
			case CCURLY:
			case SEMI:
			case ASSIGN:
			case OPAREN:
			case ASSIGN_MATCH:
			case BOOL_OR:
			case BOOL_AND:
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case DOT:
			case CPAREN:
			case OSQUARE:
			case CSQUARE:
			case COLON:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			continueExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = continueExpression_AST;
	}
	
	public final void returnExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST returnExpression_AST = null;
		
		try {      // for error handling
			AST tmp74_AST = null;
			tmp74_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp74_AST);
			match(RETURN);
			{
			if ((LA(1)==OPAREN) && (_tokenSet_29.member(LA(2)))) {
				parenExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_2.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			returnExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = returnExpression_AST;
	}
	
	public final void anonymousFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST anonymousFunction_AST = null;
		
		try {      // for error handling
			AST tmp75_AST = null;
			tmp75_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp75_AST);
			match(FN);
			{
			switch ( LA(1)) {
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case NOT:
			case INVERT:
			case IDENTIFIER:
			case LITERAL_STRING:
			case LITERAL_URI:
			case COLON:
			case GET:
			case XML_ENTITY:
			{
				defIdentifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			paramList();
			astFactory.addASTChild(currentAST, returnAST);
			blockExpression();
			astFactory.addASTChild(currentAST, returnAST);
			anonymousFunction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = anonymousFunction_AST;
	}
	
	public final void mapOrList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapOrList_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case OCURLY:
			case IF:
			case LOOP:
			case OPAREN:
			case ADD:
			case SUB:
			case NOT:
			case INVERT:
			case IDENTIFIER:
			case OSQUARE:
			case TRUE:
			case FALSE:
			case INFINITY:
			case NAN:
			case NULL:
			case LITERAL_INTEGER:
			case LITERAL_FLOAT:
			case LITERAL_STRING:
			case LITERAL_URI:
			case QUASILITERAL_STRING:
			case GET:
			case RETURN:
			case BREAK:
			case CONTINUE:
			case FN:
			case FNAME:
			{
				mapOrListElement();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop88:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						mapOrListElement();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop88;
					}
					
				} while (true);
				}
				break;
			}
			case CSQUARE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mapOrList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = mapOrList_AST;
	}
	
	public final void mapOrListElement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapOrListElement_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case COLON:
			{
				AST tmp77_AST = null;
				tmp77_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp77_AST);
				match(COLON);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CSQUARE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mapOrListElement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = mapOrListElement_AST;
	}
	
	public final void elseClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseClause_AST = null;
		
		try {      // for error handling
			match(ELSE);
			{
			{
			switch ( LA(1)) {
			case IF:
			{
				ifExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OCURLY:
			{
				blockExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			}
			elseClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = elseClause_AST;
	}
	
	public final void specialIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST specialIdentifier_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case XML_ENTITY:
			{
				AST tmp79_AST = null;
				tmp79_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp79_AST);
				match(XML_ENTITY);
				specialIdentifier_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_URI:
			{
				AST tmp80_AST = null;
				tmp80_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp80_AST);
				match(LITERAL_URI);
				specialIdentifier_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_STRING:
			{
				AST tmp81_AST = null;
				tmp81_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(LITERAL_STRING);
				specialIdentifier_AST = (AST)currentAST.root;
				break;
			}
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case AND:
			case OR:
			case XOR:
			case LSHIFT:
			case RSHIFT:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REMAINDER:
			case MOD:
			case POW:
			case NOT:
			case INVERT:
			case COLON:
			case GET:
			{
				{
				switch ( LA(1)) {
				case COLON:
				{
					match(COLON);
					AST tmp83_AST = null;
					tmp83_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp83_AST);
					match(IDENTIFIER);
					match(COLON);
					break;
				}
				case EQ:
				case NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case AND:
				case OR:
				case XOR:
				case LSHIFT:
				case RSHIFT:
				case ADD:
				case SUB:
				case MUL:
				case DIV:
				case REMAINDER:
				case MOD:
				case POW:
				case NOT:
				case INVERT:
				case GET:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case ADD:
				{
					AST tmp85_AST = null;
					tmp85_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp85_AST);
					match(ADD);
					break;
				}
				case SUB:
				{
					AST tmp86_AST = null;
					tmp86_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp86_AST);
					match(SUB);
					break;
				}
				case MUL:
				{
					AST tmp87_AST = null;
					tmp87_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp87_AST);
					match(MUL);
					break;
				}
				case POW:
				{
					AST tmp88_AST = null;
					tmp88_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp88_AST);
					match(POW);
					break;
				}
				case DIV:
				{
					AST tmp89_AST = null;
					tmp89_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp89_AST);
					match(DIV);
					break;
				}
				case REMAINDER:
				{
					AST tmp90_AST = null;
					tmp90_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp90_AST);
					match(REMAINDER);
					break;
				}
				case MOD:
				{
					AST tmp91_AST = null;
					tmp91_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp91_AST);
					match(MOD);
					break;
				}
				case AND:
				{
					AST tmp92_AST = null;
					tmp92_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp92_AST);
					match(AND);
					break;
				}
				case OR:
				{
					AST tmp93_AST = null;
					tmp93_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp93_AST);
					match(OR);
					break;
				}
				case XOR:
				{
					AST tmp94_AST = null;
					tmp94_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp94_AST);
					match(XOR);
					break;
				}
				case INVERT:
				{
					AST tmp95_AST = null;
					tmp95_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp95_AST);
					match(INVERT);
					break;
				}
				case LSHIFT:
				{
					AST tmp96_AST = null;
					tmp96_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp96_AST);
					match(LSHIFT);
					break;
				}
				case RSHIFT:
				{
					AST tmp97_AST = null;
					tmp97_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp97_AST);
					match(RSHIFT);
					break;
				}
				case NOT:
				{
					AST tmp98_AST = null;
					tmp98_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp98_AST);
					match(NOT);
					break;
				}
				case EQ:
				{
					AST tmp99_AST = null;
					tmp99_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp99_AST);
					match(EQ);
					break;
				}
				case NE:
				{
					AST tmp100_AST = null;
					tmp100_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp100_AST);
					match(NE);
					break;
				}
				case LT:
				{
					AST tmp101_AST = null;
					tmp101_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp101_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp102_AST = null;
					tmp102_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp102_AST);
					match(GT);
					break;
				}
				case LE:
				{
					AST tmp103_AST = null;
					tmp103_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp103_AST);
					match(LE);
					break;
				}
				case GE:
				{
					AST tmp104_AST = null;
					tmp104_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp104_AST);
					match(GE);
					break;
				}
				case GET:
				{
					AST tmp105_AST = null;
					tmp105_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp105_AST);
					match(GET);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				specialIdentifier_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = specialIdentifier_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"APPLY_FUNC",
		"APPLY_METH",
		"ID_QUASI",
		"AREF",
		"MAKE_LIST",
		"MAKE_MAP",
		"PARAM_LIST",
		"QUALIFIED_EXPR",
		"BLOCK_EXPR",
		"OCURLY",
		"CCURLY",
		"SEMI",
		"\"if\"",
		"\"loop\"",
		"\"def\"",
		"ASSIGN",
		"OPAREN",
		"ASSIGN_MATCH",
		"BOOL_OR",
		"BOOL_AND",
		"EQ",
		"NE",
		"LT",
		"GT",
		"LE",
		"GE",
		"AND",
		"OR",
		"XOR",
		"LSHIFT",
		"RSHIFT",
		"ADD",
		"SUB",
		"MUL",
		"DIV",
		"REMAINDER",
		"MOD",
		"POW",
		"NOT",
		"INVERT",
		"DOT",
		"IDENTIFIER",
		"CPAREN",
		"OSQUARE",
		"CSQUARE",
		"\"true\"",
		"\"false\"",
		"\"Infinity\"",
		"\"NaN\"",
		"\"null\"",
		"LITERAL_INTEGER",
		"LITERAL_FLOAT",
		"LITERAL_STRING",
		"LITERAL_URI",
		"QUASILITERAL_STRING",
		"COLON",
		"GET",
		"\"return\"",
		"\"break\"",
		"\"continue\"",
		"COMMA",
		"\"fn\"",
		"\"else\"",
		"\"fname\"",
		"XML_ENTITY",
		"WS",
		"NEWLINE",
		"XML_NAME",
		"URI_CHAR",
		"ALPHA",
		"LITERAL_NUMBER_OR_DOT",
		"FLOAT_STARTING_WITH_DOT",
		"NUMBER_STARTING_WITH_DIGIT",
		"HEX_INT",
		"DIGIT",
		"HEX_DIGIT",
		"EXPONENT",
		"CHAR_ESC",
		"STRING_NL",
		"FOUR_HEX_DIGITS"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 16386L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { -8190L, 15L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { -576834483176398848L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { -70368744185854L, 26L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { -204816L, 1048575L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { -576834483176087552L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1945607815565410304L, 16L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 49154L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 576812596024360962L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { -8190L, 11L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 1622018L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { -576834483176087550L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 8192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 576812596026982402L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 576812596031176706L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 576812596039565314L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 576812596089896962L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 576812597096529922L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 576812604612722690L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 576812630382526466L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 576812733461741570L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 576814795046043650L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 576816994069299202L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { -576764114432221184L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 576975323744747522L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 351843720888320L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 576975323744755714L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { -576553008199688192L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { -373730872975360L, 10L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 281474976710656L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 281474976710656L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	
	}
