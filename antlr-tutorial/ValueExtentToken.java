/**
 * Simple extension of {@link ExtentToken}, to add an arbitrary-value
 * field. Said value is useful for holding an interpretation of a token's
 * contents (e.g., a parsed literal value).
 *
 * <p>This file is in the public domain.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class ValueExtentToken
extends ExtentToken
{
    /** the arbitrary value associated with this token */
    private Object value;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. The instance will be of type {@link
     * #INVALID_TYPE}, have empty (<code>""</code>, not <code>null</code>)
     * text, have <code>0</code> for all position values, and have a
     * <code>null</code> value.
     */
    public ValueExtentToken ()
    {
        this ("");
    }

    /**
     * Construct an instance with the given text. The instance will be of
     * type {@link #INVALID_TYPE}, have <code>0</code> for all position
     * values, and have a <code>null</code> value.
     *
     * @param text null-ok; the token text 
     */
    public ValueExtentToken (String text)
    {
        this (INVALID_TYPE, text);
    }

    /**
     * Construct an instance with the given type and text. The instance
     * will have <code>0</code> for all position values and a
     * <code>null</code> value.
     *
     * @param type the token type
     * @param text null-ok; the token text 
     */
    public ValueExtentToken (int type, String text)
    {
        super (type, text);
        value = null;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Get the value associated with this token.
     *
     * @return null-ok; the value associated with this token
     */
    public Object getValue ()
    {
        return value;
    }

    /**
     * Set the value associated with this token.
     *
     * @param v null-ok; the new value to associate with this token
     */
    public void setValue (Object v)
    {
        value = v;
    }
}
