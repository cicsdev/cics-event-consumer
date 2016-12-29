package com.ibm.cics.ca1y.epadapterinterface;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import com.ibm.etools.marshall.util.*;

/**
 * @generated
 * Generated Class: Epdev1_epde__item
 * @type-descriptor.aggregate-instance-td accessor="readWrite" contentSize="mpy(68,val(1,1,4))" offset="12" size="67932"
 * @type-descriptor.platform-compiler-info language="COBOL" defaultBigEndian="false" defaultCodepage="ISO-8859-1" defaultExternalDecimalSign="ascii" defaultFloatType="ieeeNonExtended"
 * @type-descriptor.array-td-type lowerBound="0" stride="68" upperBound="val(1,1,4)"
 */

@SuppressWarnings( {"rawtypes", "unchecked"} )

public class Epdev1_epde__item implements javax.resource.cci.Record,
		javax.resource.cci.Streamable, com.ibm.etools.marshall.RecordBytes {
	private static final long serialVersionUID = 3657098229030394452L;
	/**
	 * @generated
	 */
	private byte[] buffer_ = null;
	/**
	 * @generated
	 */
	private static final int bufferSize_;
	/**
	 * @generated
	 */
	private static final byte[] initializedBuffer_;
	/**
	 * @generated
	 */
	private static java.util.HashMap getterMap_ = null;
	/**
	 * @generated
	 */
	private java.util.HashMap valFieldNameMap_ = null;

	/**
	 * initializer
	 * @generated
	 */
	static {
		bufferSize_ = 68;
		initializedBuffer_ = new byte[bufferSize_];
		String epde__datatypeInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epde__datatypeInitialValue, initializedBuffer_, 32,
				"ISO-8859-1", 8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT,
				" ");
		String epde__datanameInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epde__datanameInitialValue, initializedBuffer_, 0,
				"ISO-8859-1", 32,
				MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epde__formattypeInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epde__formattypeInitialValue, initializedBuffer_, 44,
				"ISO-8859-1", 16,
				MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
	}

	/**
	 * constructor
	 * @generated
	 */
	public Epdev1_epde__item() {
		initialize();
	}

	/**
	 * constructor
	 * @generated
	 */
	public Epdev1_epde__item(java.util.HashMap valFieldNameMap) {
		valFieldNameMap_ = valFieldNameMap;
		initialize();
	}

	/**
	 * @generated
	 * initialize
	 * @type-descriptor.depending-on-control fieldName="epde__itemcount" maxOccurances="999" valFormula="val(1,1,4)"
	 */
	public void initialize() {
		buffer_ = new byte[bufferSize_];
		if (valFieldNameMap_ == null) {
			java.util.HashMap valFieldNameMap = new java.util.HashMap();
			valFieldNameMap.put("val(1,1,4)", new Integer("999"));
			valFieldNameMap_ = valFieldNameMap;
		}
		System.arraycopy(initializedBuffer_, 0, buffer_, 0, bufferSize_);
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Streamable#read(java.io.InputStream)
	 */
	public void read(java.io.InputStream inputStream)
			throws java.io.IOException {
		byte[] input = new byte[inputStream.available()];
		inputStream.read(input);
		buffer_ = input;
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Streamable#write(java.io.OutputStream)
	 */
	public void write(java.io.OutputStream outputStream)
			throws java.io.IOException {
		outputStream.write(buffer_, 0, getSize());
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#getRecordName()
	 */
	public String getRecordName() {
		return (this.getClass().getName());
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#setRecordName(String)
	 */
	public void setRecordName(String recordName) {
		return;
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#setRecordShortDescription(String)
	 */
	public void setRecordShortDescription(String shortDescription) {
		return;
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#getRecordShortDescription()
	 */
	public String getRecordShortDescription() {
		return (this.getClass().getName());
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return (super.clone());
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#equals
	 */
	public boolean equals(Object object) {
		return (super.equals(object));
	}

	/**
	 * @generated
	 * @see javax.resource.cci.Record#hashCode
	 */
	public int hashCode() {
		return (super.hashCode());
	}

	/**
	 * @generated
	 * @see com.ibm.etools.marshall.RecordBytes#getBytes
	 */
	public byte[] getBytes() {
		return (buffer_);
	}

	/**
	 * @generated
	 * @see com.ibm.etools.marshall.RecordBytes#setBytes
	 */
	public void setBytes(byte[] bytes) {
		if ((bytes != null) && (bytes.length != 0))
			buffer_ = bytes;
	}

	/**
	 * @generated
	 * @see com.ibm.etools.marshall.RecordBytes#getSize
	 */
	public int getSize() {
		return (68);
	}

	/**
	 * @generated
	 */
	public boolean match(Object obj) {
		if (obj == null)
			return (false);
		if (obj.getClass().isArray()) {
			byte[] currBytes = buffer_;
			try {
				byte[] objByteArray = (byte[]) obj;
				if (objByteArray.length != buffer_.length)
					return (false);
				buffer_ = objByteArray;
			} catch (ClassCastException exc) {
				return (false);
			} finally {
				buffer_ = currBytes;
			}
		} else
			return (false);
		return (true);
	}

	/**
	 * @generated
	 */
	public void populate(Object obj) {
		if (obj.getClass().isArray()) {
			try {
				buffer_ = (byte[]) obj;
			} catch (ClassCastException exc) {
			}
		}
	}

	/**
	 * @generated
	 * @see java.lang.Object#toString
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append("\n");
		ConversionUtils.dumpBytes(sb, buffer_);
		return (sb.toString());
	}

	/**
	 * @generated
	 * wrappedGetNumber
	 */
	public Number wrappedGetNumber(String propertyName) {
		Number result = null;

		if (getterMap_ == null) {
			synchronized (initializedBuffer_) {
				if (getterMap_ == null) {
					java.util.HashMap getterMap = new java.util.HashMap();
					try {
						BeanInfo info = Introspector.getBeanInfo(this
								.getClass());
						PropertyDescriptor[] props = info
								.getPropertyDescriptors();

						for (int i = 0; i < props.length; i++) {
							String propName = props[i].getName();
							getterMap.put(propName, props[i].getReadMethod());
						}
					} catch (IntrospectionException exc) {
					}
					getterMap_ = getterMap;
				}
			}
		}

		Method method = (Method) getterMap_.get(propertyName);
		if (method != null) {
			try {
				result = (Number) method.invoke(this, new Object[0]);
			} catch (Exception exc) {
			}
		}

		return (result);
	}

	/**
	 * @generated
	 * evaluateMap
	 */
	public java.util.HashMap evaluateMap(java.util.HashMap valFieldNameMap) {
		if (valFieldNameMap == null)
			return (null);
		java.util.HashMap returnMap = new java.util.HashMap(
				valFieldNameMap.size());
		java.util.Set aSet = valFieldNameMap.entrySet();

		for (java.util.Iterator cursor = aSet.iterator(); cursor.hasNext();) {
			java.util.Map.Entry element = (java.util.Map.Entry) cursor.next();
			String key = (String) element.getKey();
			String fieldName = (String) element.getValue();
			Number fieldValue = wrappedGetNumber(fieldName);
			if (fieldValue == null)
				fieldValue = new Integer(0);
			returnMap.put(key, fieldValue);
		}

		return (returnMap);
	}

	/**
	 * @generated
	 * Returns the integer value of the formula string for an offset or size.
	 * The formula can be comprised of the following functions:
	 * neg(x)   := -x       // prefix negate
	 * add(x,y) := x+y      // infix add
	 * sub(x,y) := x-y      // infix subtract
	 * mpy(x,y) := x*y      // infix multiply
	 * div(x,y) := x/y      // infix divide
	 * max(x,y) := max(x,y)
	 * min(x,y) := min(x,y)
	 *
	 * mod(x,y) := x mod y
	 *
	 * The mod function is defined as mod(x,y) = r where r is the smallest non-negative integer
	 * such that x-r is evenly divisible by y. So mod(7,4) is 3, but mod(-7,4) is 1. If y is a
	 * power of 2, then mod(x,y) is equal to the bitwise-and of x and y-1.
	 *
	 * val(1, m, n, o,..)
	 *
	 * The val function returns the value of a field in the model. The val function takes one
	 * or more arguments, and the first argument refers to a level-1 field in the type model and must be either:
	 *    - the name of a level-1 field described in the language model
	 *    - the integer 1 (indicating that the level-1 parent of the current structure is meant)
	 * If the first argument to the val function is the integer 1, then and only then are subsequent arguments
	 * permitted. These subsequent arguments are integers that the specify the ordinal number within its
	 * substructure of the subfield that should be dereferenced.
	 *
	 * @return The integer value of the formula string for an offset or size.
	 * @param formula The formula to be evaluated.
	 * @param valFieldNameMap A map of val() formulas to field names.
	 * @throws IllegalArgumentException if the formula is null.
	 */

	public int evaluateFormula(String formula, java.util.HashMap valFieldNameMap)
			throws IllegalArgumentException {
		if (formula == null)
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.MARSHRT_FORMULA_NULL));

		int result = 0;

		int index = formula.indexOf("(");

		if (index == -1) // It's a number not an expression
		{
			try {
				result = Integer.parseInt(formula);
			} catch (Exception exc) {
			}

			return (result);
		}

		// Determine the outermost function
		String function = formula.substring(0, index);

		if (function.equalsIgnoreCase("val")) {
			Object field = valFieldNameMap.get(formula);
			if (field == null)
				return (0);

			if (field instanceof String) {
				Number num = wrappedGetNumber((String) field);
				if (num == null) // Element does not exist
					return (0);
				result = num.intValue();
			} else if (field instanceof Number)
				result = ((Number) field).intValue();
			else
				return (0);

			return (result);
		} else if (function.equalsIgnoreCase("neg")) {
			// The new formula is the content between the brackets
			formula = formula.substring(index + 1, formula.length() - 1);
			result = -1 * evaluateFormula(formula, valFieldNameMap);
			return (result);
		} else {
			// Get the contents between the outermost brackets
			formula = formula.substring(index + 1, formula.length() - 1);
			char[] formulaChars = formula.toCharArray();

			// Get the left side and the right side of the operation

			int brackets = 0;
			int i = 0;

			for (; i < formulaChars.length; i++) {
				if (formulaChars[i] == '(')
					brackets++;
				else if (formulaChars[i] == ')')
					brackets--;
				else if (formulaChars[i] == ',') {
					if (brackets == 0)
						break;
				}
			}

			String leftSide = "0";
			String rightSide = "0";

			leftSide = formula.substring(0, i);
			rightSide = formula.substring(i + 1);

			if (function.equalsIgnoreCase("add"))
				result = evaluateFormula(leftSide, valFieldNameMap)
						+ evaluateFormula(rightSide, valFieldNameMap);
			else if (function.equalsIgnoreCase("mpy"))
				result = evaluateFormula(leftSide, valFieldNameMap)
						* evaluateFormula(rightSide, valFieldNameMap);
			else if (function.equalsIgnoreCase("sub"))
				result = evaluateFormula(leftSide, valFieldNameMap)
						- evaluateFormula(rightSide, valFieldNameMap);
			else if (function.equalsIgnoreCase("div"))
				result = evaluateFormula(leftSide, valFieldNameMap)
						/ evaluateFormula(rightSide, valFieldNameMap);
			else if (function.equalsIgnoreCase("max"))
				result = Math.max(evaluateFormula(leftSide, valFieldNameMap),
						evaluateFormula(rightSide, valFieldNameMap));
			else if (function.equalsIgnoreCase("min"))
				result = Math.min(evaluateFormula(leftSide, valFieldNameMap),
						evaluateFormula(rightSide, valFieldNameMap));
			else if (function.equalsIgnoreCase("mod"))
				result = evaluateFormula(leftSide, valFieldNameMap)
						% evaluateFormula(rightSide, valFieldNameMap);
		}

		return (result);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="32"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="32" offset="12" size="32"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpde__dataname() {
		String epde__dataname = null;
		epde__dataname = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 0,
						"ISO-8859-1", 32);
		return (epde__dataname);
	}

	/**
	 * @generated
	 */
	public void setEpde__dataname(String epde__dataname) {
		if (epde__dataname != null) {
			if (epde__dataname.length() > 32)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epde__dataname,
								"32", "epde__dataname"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epde__dataname, buffer_, 0, "ISO-8859-1", 32,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.level88 name="epde__charz" value="CHARZ\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__hexz" value="HEXZ\u0020\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__decfloat" value="DECFLOAT"
	 * @type-descriptor.level88 name="epde__binfloat" value="BINFLOAT"
	 * @type-descriptor.level88 name="epde__hexfloat" value="HEXFLOAT"
	 * @type-descriptor.level88 name="epde__char" value="CHAR\u0020\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__sfword" value="SFWORD\u0020\u0020"
	 * @type-descriptor.level88 name="epde__shword" value="SHWORD\u0020\u0020"
	 * @type-descriptor.level88 name="epde__ufword" value="UFWORD\u0020\u0020"
	 * @type-descriptor.level88 name="epde__uhword" value="UHWORD\u0020\u0020"
	 * @type-descriptor.level88 name="epde__hex" value="HEX\u0020\u0020\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__zoned" value="ZONED\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__packed" value="PACKED\u0020\u0020"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="44" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpde__datatype() {
		String epde__datatype = null;
		epde__datatype = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 32,
						"ISO-8859-1", 8);
		return (epde__datatype);
	}

	/**
	 * @generated
	 */
	public void setEpde__datatype(String epde__datatype) {
		if (epde__datatype != null) {
			if (epde__datatype.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epde__datatype,
								"8", "epde__datatype"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epde__datatype, buffer_, 32, "ISO-8859-1", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 */
	public String getEpde__charz() {
		return ("CHARZ   ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__charz(String epde__charz) {
		return ("CHARZ   ".equals(epde__charz));
	}

	/**
	 * @generated
	 */
	public String getEpde__hexz() {
		return ("HEXZ    ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__hexz(String epde__hexz) {
		return ("HEXZ    ".equals(epde__hexz));
	}

	/**
	 * @generated
	 */
	public String getEpde__decfloat() {
		return ("DECFLOAT");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__decfloat(String epde__decfloat) {
		return ("DECFLOAT".equals(epde__decfloat));
	}

	/**
	 * @generated
	 */
	public String getEpde__binfloat() {
		return ("BINFLOAT");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__binfloat(String epde__binfloat) {
		return ("BINFLOAT".equals(epde__binfloat));
	}

	/**
	 * @generated
	 */
	public String getEpde__hexfloat() {
		return ("HEXFLOAT");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__hexfloat(String epde__hexfloat) {
		return ("HEXFLOAT".equals(epde__hexfloat));
	}

	/**
	 * @generated
	 */
	public String getEpde__char() {
		return ("CHAR    ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__char(String epde__char) {
		return ("CHAR    ".equals(epde__char));
	}

	/**
	 * @generated
	 */
	public String getEpde__sfword() {
		return ("SFWORD  ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__sfword(String epde__sfword) {
		return ("SFWORD  ".equals(epde__sfword));
	}

	/**
	 * @generated
	 */
	public String getEpde__shword() {
		return ("SHWORD  ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__shword(String epde__shword) {
		return ("SHWORD  ".equals(epde__shword));
	}

	/**
	 * @generated
	 */
	public String getEpde__ufword() {
		return ("UFWORD  ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__ufword(String epde__ufword) {
		return ("UFWORD  ".equals(epde__ufword));
	}

	/**
	 * @generated
	 */
	public String getEpde__uhword() {
		return ("UHWORD  ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__uhword(String epde__uhword) {
		return ("UHWORD  ".equals(epde__uhword));
	}

	/**
	 * @generated
	 */
	public String getEpde__hex() {
		return ("HEX     ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__hex(String epde__hex) {
		return ("HEX     ".equals(epde__hex));
	}

	/**
	 * @generated
	 */
	public String getEpde__zoned() {
		return ("ZONED   ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__zoned(String epde__zoned) {
		return ("ZONED   ".equals(epde__zoned));
	}

	/**
	 * @generated
	 */
	public String getEpde__packed() {
		return ("PACKED  ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__packed(String epde__packed) {
		return ("PACKED  ".equals(epde__packed));
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="52" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpde__dataprecision() {
		int epde__dataprecision = 0;
		epde__dataprecision = MarshallIntegerUtils
				.unmarshallFourByteIntegerFromBuffer(buffer_, 40, false,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__dataprecision);
	}

	/**
	 * @generated
	 */
	public void setEpde__dataprecision(int epde__dataprecision) {
		if ((epde__dataprecision < -99999999)
				|| (epde__dataprecision > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epde__dataprecision),
							"epde__dataprecision", "-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(
				epde__dataprecision, buffer_, 40, false,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="16"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.level88 name="epde__scientific" value="scientific\u0020\u0020\u0020\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__numeric" value="numeric\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020"
	 * @type-descriptor.level88 name="epde__text" value="text\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="16" offset="56" size="16"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpde__formattype() {
		String epde__formattype = null;
		epde__formattype = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 44,
						"ISO-8859-1", 16);
		return (epde__formattype);
	}

	/**
	 * @generated
	 */
	public void setEpde__formattype(String epde__formattype) {
		if (epde__formattype != null) {
			if (epde__formattype.length() > 16)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E,
								epde__formattype, "16", "epde__formattype"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epde__formattype, buffer_, 44, "ISO-8859-1", 16,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 */
	public String getEpde__scientific() {
		return ("scientific      ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__scientific(String epde__scientific) {
		return ("scientific      ".equals(epde__scientific));
	}

	/**
	 * @generated
	 */
	public String getEpde__numeric() {
		return ("numeric         ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__numeric(String epde__numeric) {
		return ("numeric         ".equals(epde__numeric));
	}

	/**
	 * @generated
	 */
	public String getEpde__text() {
		return ("text            ");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__text(String epde__text) {
		return ("text            ".equals(epde__text));
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.level88 name="epde__formatlen__auto" value="0"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="72" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpde__formatlen() {
		int epde__formatlen = 0;
		epde__formatlen = MarshallIntegerUtils
				.unmarshallFourByteIntegerFromBuffer(buffer_, 60, false,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__formatlen);
	}

	/**
	 * @generated
	 */
	public void setEpde__formatlen(int epde__formatlen) {
		if ((epde__formatlen < -99999999) || (epde__formatlen > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epde__formatlen),
							"epde__formatlen", "-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(epde__formatlen,
				buffer_, 60, false,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public int getEpde__formatlen__auto() {
		return (0);
	}

	/**
	 * @generated
	 */
	public boolean isEpde__formatlen__auto(int epde__formatlen__auto) {
		return (epde__formatlen__auto == 0);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.level88 name="epde__formatprec__auto" value="-1"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="76" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpde__formatprecision() {
		int epde__formatprecision = 0;
		epde__formatprecision = MarshallIntegerUtils
				.unmarshallFourByteIntegerFromBuffer(buffer_, 64, false,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__formatprecision);
	}

	/**
	 * @generated
	 */
	public void setEpde__formatprecision(int epde__formatprecision) {
		if ((epde__formatprecision < -99999999)
				|| (epde__formatprecision > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epde__formatprecision),
							"epde__formatprecision", "-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(
				epde__formatprecision, buffer_, 64, false,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public int getEpde__formatprec__auto() {
		return (-1);
	}

	/**
	 * @generated
	 */
	public boolean isEpde__formatprec__auto(int epde__formatprec__auto) {
		return (epde__formatprec__auto == -1);
	}

}