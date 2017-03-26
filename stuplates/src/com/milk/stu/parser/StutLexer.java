// $ANTLR 2.7.2a2 (20020112-1): "stut.g" -> "StutLexer.java"$

    package com.milk.stu.parser;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class StutLexer extends antlr.CharScanner implements StutLexerTokenTypes, TokenStream
 {
public StutLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public StutLexer(Reader in) {
	this(new CharBuffer(in));
}
public StutLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public StutLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				if ((LA(1)=='$') && (LA(2)=='{')) {
					mTEMPLATE_BLOCK(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && (true)) {
					mLITERAL_TEXT(true);
					theRetToken=_returnToken;
				}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mLITERAL_TEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LITERAL_TEXT;
		int _saveIndex;
		
		if ((LA(1)=='$') && (LA(2)=='$')) {
			mLITERAL_ESCAPE_CHAR(false);
		}
		else if ((LA(1)=='$') && (LA(2)=='\\')) {
			mCHAR_ESCAPE_SEQUENCE(false);
		}
		else if ((_tokenSet_0.member(LA(1)))) {
			mLITERAL_CHAR(false);
		}
		else if ((LA(1)=='$') && (true)) {
			mFAKIE_ESCAPE(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLITERAL_CHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LITERAL_CHAR;
		int _saveIndex;
		
		if ((_tokenSet_1.member(LA(1)))) {
			{
			match(_tokenSet_1);
			}
		}
		else if ((LA(1)=='\n'||LA(1)=='\r')) {
			mNEWLINE(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLITERAL_ESCAPE_CHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LITERAL_ESCAPE_CHAR;
		int _saveIndex;
		
		_saveIndex=text.length();
		match("$$");
		text.setLength(_saveIndex);
		if ( inputState.guessing==0 ) {
			text.setLength(_begin); text.append("$");
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCHAR_ESCAPE_SEQUENCE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CHAR_ESCAPE_SEQUENCE;
		int _saveIndex;
		Token digs=null;
		char  anythingElse = '\0';
		
		_saveIndex=text.length();
		match("$\\");
		text.setLength(_saveIndex);
		{
		switch ( LA(1)) {
		case '\n':  case '\r':
		{
			_saveIndex=text.length();
			mNEWLINE(false);
			text.setLength(_saveIndex);
			{
			_loop25:
			do {
				switch ( LA(1)) {
				case ' ':
				{
					_saveIndex=text.length();
					match(' ');
					text.setLength(_saveIndex);
					break;
				}
				case '\t':
				{
					_saveIndex=text.length();
					match('\t');
					text.setLength(_saveIndex);
					break;
				}
				default:
				{
					break _loop25;
				}
				}
			} while (true);
			}
			break;
		}
		case 'b':
		{
			_saveIndex=text.length();
			match('b');
			text.setLength(_saveIndex);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\b");
			}
			break;
		}
		case 'f':
		{
			_saveIndex=text.length();
			match('f');
			text.setLength(_saveIndex);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\f");
			}
			break;
		}
		case 'n':
		{
			_saveIndex=text.length();
			match('n');
			text.setLength(_saveIndex);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\n");
			}
			break;
		}
		case 'r':
		{
			_saveIndex=text.length();
			match('r');
			text.setLength(_saveIndex);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\r");
			}
			break;
		}
		case 't':
		{
			_saveIndex=text.length();
			match('t');
			text.setLength(_saveIndex);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\t");
			}
			break;
		}
		case 'u':
		{
			_saveIndex=text.length();
			match('u');
			text.setLength(_saveIndex);
			_saveIndex=text.length();
			mFOUR_HEX_DIGITS(true);
			text.setLength(_saveIndex);
			digs=_returnToken;
			if ( inputState.guessing==0 ) {
				String dstr = digs.getText ();
				char[] carr = { (char) Integer.parseInt (dstr, 16) };
				String txt = new String (carr);
				text.setLength(_begin); text.append(txt);
				
			}
			break;
		}
		default:
			if ((_tokenSet_2.member(LA(1)))) {
				{
				anythingElse = LA(1);
				match(_tokenSet_2);
				}
				if ( inputState.guessing==0 ) {
					text.setLength(_begin); text.append(anythingElse);
				}
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFAKIE_ESCAPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FAKIE_ESCAPE;
		int _saveIndex;
		
		match('$');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTEMPLATE_BLOCK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPLATE_BLOCK;
		int _saveIndex;
		
		boolean synPredMatched7 = false;
		if (((LA(1)=='$') && (LA(2)=='{') && ((LA(3) >= '\u0000' && LA(3) <= '\u00ff')))) {
			int _m7 = mark();
			synPredMatched7 = true;
			inputState.guessing++;
			try {
				{
				match("${");
				{
				if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && (_tokenSet_3.member(LA(2)))) {
					matchNot(EOF_CHAR);
					{
					match(_tokenSet_3);
					}
				}
				else if ((_tokenSet_3.member(LA(1))) && (true)) {
					{
					match(_tokenSet_3);
					}
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched7 = false;
			}
			rewind(_m7);
			inputState.guessing--;
		}
		if ( synPredMatched7 ) {
			_saveIndex=text.length();
			match("${");
			text.setLength(_saveIndex);
			mTEMPLATE_BODY(false);
			_saveIndex=text.length();
			match("}");
			text.setLength(_saveIndex);
		}
		else {
			boolean synPredMatched9 = false;
			if (((LA(1)=='$') && (LA(2)=='{') && (LA(3)=='-'))) {
				int _m9 = mark();
				synPredMatched9 = true;
				inputState.guessing++;
				try {
					{
					match("${--");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched9 = false;
				}
				rewind(_m9);
				inputState.guessing--;
			}
			if ( synPredMatched9 ) {
				_saveIndex=text.length();
				match("${--");
				text.setLength(_saveIndex);
				{
				_loop11:
				do {
					// nongreedy exit test
					if ((LA(1)=='-') && (LA(2)=='-') && (LA(3)=='}')) break _loop11;
					if (((LA(1) >= '\u0000' && LA(1) <= '\u00ff')) && ((LA(2) >= '\u0000' && LA(2) <= '\u00ff')) && ((LA(3) >= '\u0000' && LA(3) <= '\u00ff'))) {
						_saveIndex=text.length();
						mCOMMENT_TEXT(false);
						text.setLength(_saveIndex);
					}
					else {
						break _loop11;
					}
					
				} while (true);
				}
				_saveIndex=text.length();
				match("--}");
				text.setLength(_saveIndex);
				if ( inputState.guessing==0 ) {
					_ttype = Token.SKIP;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	protected final void mTEMPLATE_BODY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPLATE_BODY;
		int _saveIndex;
		
		{
		_loop32:
		do {
			if ((_tokenSet_4.member(LA(1)))) {
				mTEMPL_BODY_PART(false);
			}
			else {
				break _loop32;
			}
			
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCOMMENT_TEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT_TEXT;
		int _saveIndex;
		
		if ((_tokenSet_5.member(LA(1)))) {
			{
			{
			match(_tokenSet_5);
			}
			}
		}
		else if ((LA(1)=='\n'||LA(1)=='\r')) {
			_saveIndex=text.length();
			mNEWLINE(false);
			text.setLength(_saveIndex);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNEWLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NEWLINE;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n') && (true) && (true)) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			newline ();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFOUR_HEX_DIGITS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FOUR_HEX_DIGITS;
		int _saveIndex;
		
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':
		{
			matchRange('A','F');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':
		{
			matchRange('a','f');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_BODY_PART(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_BODY_PART;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '\'':
		{
			match('\'');
			{
			_loop35:
			do {
				if ((_tokenSet_6.member(LA(1)))) {
					mTEMPL_STRING_CHAR1(false);
				}
				else {
					break _loop35;
				}
				
			} while (true);
			}
			match('\'');
			break;
		}
		case '"':
		{
			match('\"');
			{
			_loop37:
			do {
				if ((_tokenSet_7.member(LA(1)))) {
					mTEMPL_STRING_CHAR2(false);
				}
				else {
					break _loop37;
				}
				
			} while (true);
			}
			match('\"');
			break;
		}
		case '`':
		{
			match('`');
			{
			_loop39:
			do {
				if ((_tokenSet_8.member(LA(1)))) {
					mTEMPL_STRING_CHAR3(false);
				}
				else {
					break _loop39;
				}
				
			} while (true);
			}
			match('`');
			break;
		}
		case '{':
		{
			match('{');
			mTEMPLATE_BODY(false);
			match('}');
			break;
		}
		case '#':
		{
			match('#');
			{
			_loop41:
			do {
				if ((_tokenSet_5.member(LA(1)))) {
					mTEMPL_COMMENT_CHAR(false);
				}
				else {
					break _loop41;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case '\r':
			{
				match('\r');
				break;
			}
			case '\n':
			{
				match('\n');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			break;
		}
		default:
			if ((_tokenSet_9.member(LA(1)))) {
				{
				match(_tokenSet_9);
				}
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_STRING_CHAR1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_STRING_CHAR1;
		int _saveIndex;
		
		if ((LA(1)=='\n'||LA(1)=='\r'||LA(1)=='\\')) {
			mTEMPL_STRING_ESCAPE_OR_NL(false);
		}
		else if ((_tokenSet_10.member(LA(1)))) {
			{
			match(_tokenSet_10);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_STRING_CHAR2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_STRING_CHAR2;
		int _saveIndex;
		
		if ((LA(1)=='\n'||LA(1)=='\r'||LA(1)=='\\')) {
			mTEMPL_STRING_ESCAPE_OR_NL(false);
		}
		else if ((_tokenSet_11.member(LA(1)))) {
			{
			match(_tokenSet_11);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_STRING_CHAR3(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_STRING_CHAR3;
		int _saveIndex;
		
		if ((LA(1)=='\n'||LA(1)=='\r'||LA(1)=='\\')) {
			mTEMPL_STRING_ESCAPE_OR_NL(false);
		}
		else if ((_tokenSet_12.member(LA(1)))) {
			{
			match(_tokenSet_12);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_COMMENT_CHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_COMMENT_CHAR;
		int _saveIndex;
		
		{
		match(_tokenSet_5);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTEMPL_STRING_ESCAPE_OR_NL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEMPL_STRING_ESCAPE_OR_NL;
		int _saveIndex;
		
		if ((LA(1)=='\\') && (_tokenSet_5.member(LA(2)))) {
			match('\\');
			{
			match(_tokenSet_5);
			}
		}
		else if ((LA(1)=='\\') && (LA(2)=='\n'||LA(2)=='\r')) {
			match('\\');
			mNEWLINE(false);
		}
		else if ((LA(1)=='\n'||LA(1)=='\r')) {
			mNEWLINE(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[0]=-68719476737L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=-68719485953L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=-9217L;
		data[1]=-14707359590907905L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[0]=-35184372088833L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=-1L;
		data[1]=-2305843009213693953L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[8];
		data[0]=-9217L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-549755813889L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=-17179869185L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=-1L;
		data[1]=-4294967297L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[8];
		data[0]=-601295421441L;
		data[1]=-2882303765812084737L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[8];
		data[0]=-549755823105L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = new long[8];
		data[0]=-17179878401L;
		data[1]=-268435457L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = new long[8];
		data[0]=-9217L;
		data[1]=-4563402753L;
		for (int i = 2; i<=3; i++) { data[i]=-1L; }
		for (int i = 4; i<=7; i++) { data[i]=0L; }
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	
	}
