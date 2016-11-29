package com.ibm.cics.ca1y.epadapterinterface;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import com.ibm.etools.marshall.util.*;

/**
 * @generated
 * Generated Class: EPCXv1
 * @type-descriptor.aggregate-instance-td accessor="readWrite" contentSize="191" offset="0" size="191"
 * @type-descriptor.platform-compiler-info language="COBOL" defaultBigEndian="true" defaultCodepage="IBM-037" defaultExternalDecimalSign="ebcdic" defaultFloatType="ibm390Hex"
 */

@SuppressWarnings( {"rawtypes", "unused", "unchecked"} )

public class EPCXv1 implements javax.resource.cci.Record,
		javax.resource.cci.Streamable, com.ibm.etools.marshall.RecordBytes {
	private static final long serialVersionUID = -3849605392448039151L;
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
		bufferSize_ = 191;
		initializedBuffer_ = new byte[bufferSize_];
		String epcx__programInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__programInitialValue, initializedBuffer_, 120, "IBM-037",
				8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__applidInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__applidInitialValue, initializedBuffer_, 152, "IBM-037",
				8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__tranidInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__tranidInitialValue, initializedBuffer_, 116, "IBM-037",
				4, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__useridInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__useridInitialValue, initializedBuffer_, 128, "IBM-037",
				8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__event__bindingInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__event__bindingInitialValue, initializedBuffer_, 12,
				"IBM-037", 32, MarshallStringUtils.STRING_JUSTIFICATION_LEFT,
				" ");
		String epcx__netqualInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__netqualInitialValue, initializedBuffer_, 144, "IBM-037",
				8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__ebusertagInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__ebusertagInitialValue, initializedBuffer_, 76, "IBM-037",
				8, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__uowidInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__uowidInitialValue, initializedBuffer_, 164, "IBM-037",
				27, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__businesseventInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__businesseventInitialValue, initializedBuffer_, 84,
				"IBM-037", 32, MarshallStringUtils.STRING_JUSTIFICATION_LEFT,
				" ");
		String epcx__strucidInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__strucidInitialValue, initializedBuffer_, 0, "IBM-037", 4,
				MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		String epcx__cs__nameInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epcx__cs__nameInitialValue, initializedBuffer_, 44, "IBM-037",
				32, MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
	}

	/**
	 * constructor
	 * @generated
	 */
	public EPCXv1() {
		initialize();
	}

	/**
	 * constructor
	 * @generated
	 */
	public EPCXv1(java.util.HashMap valFieldNameMap) {
		valFieldNameMap_ = valFieldNameMap;
		initialize();
	}

	/**
	 * @generated
	 * initialize
	 */
	public void initialize() {
		buffer_ = new byte[bufferSize_];
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
		return (191);
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
		java.util.HashMap returnMap = new java.util.HashMap(valFieldNameMap
				.size());
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
	 * @type-descriptor.restriction maxLength="4"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.level88 name="epcx__struc__id" value="EPCX"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="0" size="4"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__strucid() {
		String epcx__strucid = null;
		epcx__strucid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 0, "IBM-037", 4);
		return (epcx__strucid);
	}

	/**
	 * @generated
	 */
	public void setEpcx__strucid(String epcx__strucid) {
		if (epcx__strucid != null) {
			if (epcx__strucid.length() > 4)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__strucid,
								"4", "epcx__strucid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__strucid, buffer_, 0, "IBM-037", 4,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 */
	public String getEpcx__struc__id() {
		return ("EPCX");
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__struc__id(String epcx__struc__id) {
		return ("EPCX".equals(epcx__struc__id));
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.level88 name="epcx__current__version" value="1"
	 * @type-descriptor.level88 name="epcx__version__1" value="1"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="4" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpcx__version() {
		int epcx__version = 0;
		epcx__version = MarshallIntegerUtils
				.unmarshallFourByteIntegerFromBuffer(buffer_, 4, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epcx__version);
	}

	/**
	 * @generated
	 */
	public void setEpcx__version(int epcx__version) {
		if ((epcx__version < -99999999) || (epcx__version > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epcx__version), "epcx__version",
							"-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(epcx__version,
				buffer_, 4, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public int getEpcx__current__version() {
		return (1);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__current__version(int epcx__current__version) {
		return (epcx__current__version == 1);
	}

	/**
	 * @generated
	 */
	public int getEpcx__version__1() {
		return (1);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__version__1(int epcx__version__1) {
		return (epcx__version__1 == 1);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-9999" upperBound="9999"
	 * @type-descriptor.level88 name="epcx__current__schema__version" value="1"
	 * @type-descriptor.level88 name="epcx__schema__version__1" value="1"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="2" offset="8" size="2"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public short getEpcx__schema__version() {
		short epcx__schema__version = 0;
		epcx__schema__version = MarshallIntegerUtils
				.unmarshallTwoByteIntegerFromBuffer(buffer_, 8, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epcx__schema__version);
	}

	/**
	 * @generated
	 */
	public void setEpcx__schema__version(short epcx__schema__version) {
		if ((epcx__schema__version < -9999) || (epcx__schema__version > 9999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Short.toString(epcx__schema__version),
							"epcx__schema__version", "-9999", "9999"));
		MarshallIntegerUtils.marshallTwoByteIntegerIntoBuffer(
				epcx__schema__version, buffer_, 8, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public short getEpcx__current__schema__version() {
		return ((short) 1);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__current__schema__version(
			short epcx__current__schema__version) {
		return (epcx__current__schema__version == (short) 1);
	}

	/**
	 * @generated
	 */
	public short getEpcx__schema__version__1() {
		return ((short) 1);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__schema__version__1(short epcx__schema__version__1) {
		return (epcx__schema__version__1 == (short) 1);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-9999" upperBound="9999"
	 * @type-descriptor.level88 name="epcx__current__schema__release" value="0"
	 * @type-descriptor.level88 name="epcx__schema__release__0" value="0"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="2" offset="10" size="2"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public short getEpcx__schema__release() {
		short epcx__schema__release = 0;
		epcx__schema__release = MarshallIntegerUtils
				.unmarshallTwoByteIntegerFromBuffer(buffer_, 10, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epcx__schema__release);
	}

	/**
	 * @generated
	 */
	public void setEpcx__schema__release(short epcx__schema__release) {
		if ((epcx__schema__release < -9999) || (epcx__schema__release > 9999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Short.toString(epcx__schema__release),
							"epcx__schema__release", "-9999", "9999"));
		MarshallIntegerUtils.marshallTwoByteIntegerIntoBuffer(
				epcx__schema__release, buffer_, 10, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public short getEpcx__current__schema__release() {
		return ((short) 0);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__current__schema__release(
			short epcx__current__schema__release) {
		return (epcx__current__schema__release == (short) 0);
	}

	/**
	 * @generated
	 */
	public short getEpcx__schema__release__0() {
		return ((short) 0);
	}

	/**
	 * @generated
	 */
	public boolean isEpcx__schema__release__0(short epcx__schema__release__0) {
		return (epcx__schema__release__0 == (short) 0);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="32"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="32" offset="12" size="32"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__event__binding() {
		String epcx__event__binding = null;
		epcx__event__binding = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 12, "IBM-037",
						32);
		return (epcx__event__binding);
	}

	/**
	 * @generated
	 */
	public void setEpcx__event__binding(String epcx__event__binding) {
		if (epcx__event__binding != null) {
			if (epcx__event__binding.length() > 32)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E,
								epcx__event__binding, "32",
								"epcx__event__binding"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__event__binding, buffer_, 12, "IBM-037", 32,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="32"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="32" offset="44" size="32"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__cs__name() {
		String epcx__cs__name = null;
		epcx__cs__name = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 44, "IBM-037",
						32);
		return (epcx__cs__name);
	}

	/**
	 * @generated
	 */
	public void setEpcx__cs__name(String epcx__cs__name) {
		if (epcx__cs__name != null) {
			if (epcx__cs__name.length() > 32)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__cs__name,
								"32", "epcx__cs__name"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__cs__name, buffer_, 44, "IBM-037", 32,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="76" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__ebusertag() {
		String epcx__ebusertag = null;
		epcx__ebusertag = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 76, "IBM-037",
						8);
		return (epcx__ebusertag);
	}

	/**
	 * @generated
	 */
	public void setEpcx__ebusertag(String epcx__ebusertag) {
		if (epcx__ebusertag != null) {
			if (epcx__ebusertag.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__ebusertag,
								"8", "epcx__ebusertag"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__ebusertag, buffer_, 76, "IBM-037", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="32"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="32" offset="84" size="32"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__businessevent() {
		String epcx__businessevent = null;
		epcx__businessevent = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 84, "IBM-037",
						32);
		return (epcx__businessevent);
	}

	/**
	 * @generated
	 */
	public void setEpcx__businessevent(String epcx__businessevent) {
		if (epcx__businessevent != null) {
			if (epcx__businessevent.length() > 32)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E,
								epcx__businessevent, "32",
								"epcx__businessevent"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__businessevent, buffer_, 84, "IBM-037", 32,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="4"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="116" size="4"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__tranid() {
		String epcx__tranid = null;
		epcx__tranid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 116, "IBM-037",
						4);
		return (epcx__tranid);
	}

	/**
	 * @generated
	 */
	public void setEpcx__tranid(String epcx__tranid) {
		if (epcx__tranid != null) {
			if (epcx__tranid.length() > 4)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__tranid,
								"4", "epcx__tranid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__tranid, buffer_, 116, "IBM-037", 4,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="120" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__program() {
		String epcx__program = null;
		epcx__program = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 120, "IBM-037",
						8);
		return (epcx__program);
	}

	/**
	 * @generated
	 */
	public void setEpcx__program(String epcx__program) {
		if (epcx__program != null) {
			if (epcx__program.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__program,
								"8", "epcx__program"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__program, buffer_, 120, "IBM-037", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="128" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__userid() {
		String epcx__userid = null;
		epcx__userid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 128, "IBM-037",
						8);
		return (epcx__userid);
	}

	/**
	 * @generated
	 */
	public void setEpcx__userid(String epcx__userid) {
		if (epcx__userid != null) {
			if (epcx__userid.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__userid,
								"8", "epcx__userid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__userid, buffer_, 128, "IBM-037", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-999999999999999" upperBound="999999999999999"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="136" size="8"
	 * @type-descriptor.packed-decimal-td
	 */
	public long getEpcx__abstime() {
		long epcx__abstime = 0;
		epcx__abstime = MarshallPackedDecimalUtils.unmarshallLongFromBuffer(
				buffer_, 136, 8);
		return (epcx__abstime);
	}

	/**
	 * @generated
	 */
	public void setEpcx__abstime(long epcx__abstime) {
		if ((epcx__abstime < -999999999999999L)
				|| (epcx__abstime > 999999999999999L))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Long.toString(epcx__abstime), "epcx__abstime",
							"-999999999999999", "999999999999999"));
		MarshallPackedDecimalUtils.marshallPackedDecimalIntoBuffer(
				epcx__abstime, buffer_, 136, 8, true);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="144" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__netqual() {
		String epcx__netqual = null;
		epcx__netqual = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 144, "IBM-037",
						8);
		return (epcx__netqual);
	}

	/**
	 * @generated
	 */
	public void setEpcx__netqual(String epcx__netqual) {
		if (epcx__netqual != null) {
			if (epcx__netqual.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__netqual,
								"8", "epcx__netqual"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__netqual, buffer_, 144, "IBM-037", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="8"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="8" offset="152" size="8"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__applid() {
		String epcx__applid = null;
		epcx__applid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 152, "IBM-037",
						8);
		return (epcx__applid);
	}

	/**
	 * @generated
	 */
	public void setEpcx__applid(String epcx__applid) {
		if (epcx__applid != null) {
			if (epcx__applid.length() > 8)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__applid,
								"8", "epcx__applid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__applid, buffer_, 152, "IBM-037", 8,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="160" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpcx__resp() {
		int epcx__resp = 0;
		epcx__resp = MarshallIntegerUtils.unmarshallFourByteIntegerFromBuffer(
				buffer_, 160, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epcx__resp);
	}

	/**
	 * @generated
	 */
	public void setEpcx__resp(int epcx__resp) {
		if ((epcx__resp < -99999999) || (epcx__resp > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epcx__resp), "epcx__resp",
							"-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(epcx__resp,
				buffer_, 160, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction maxLength="27"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="27" offset="164" size="27"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpcx__uowid() {
		String epcx__uowid = null;
		epcx__uowid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 164, "IBM-037",
						27);
		return (epcx__uowid);
	}

	/**
	 * @generated
	 */
	public void setEpcx__uowid(String epcx__uowid) {
		if (epcx__uowid != null) {
			if (epcx__uowid.length() > 27)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epcx__uowid,
								"27", "epcx__uowid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epcx__uowid, buffer_, 164, "IBM-037", 27,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

}