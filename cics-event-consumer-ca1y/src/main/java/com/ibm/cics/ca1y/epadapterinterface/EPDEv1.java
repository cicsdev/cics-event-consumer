package com.ibm.cics.ca1y.epadapterinterface;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import com.ibm.etools.marshall.util.*;

/**
 * @generated
 * Generated Class: EPDE
 * @type-descriptor.aggregate-instance-td accessor="readWrite" contentSize="add(12,mpy(68,val(1,1,4)))" offset="0" size="67944"
 * @type-descriptor.platform-compiler-info language="COBOL" defaultBigEndian="true" defaultCodepage="IBM-037" defaultExternalDecimalSign="ebcdic" defaultFloatType="ibm390Hex"
 * @type-descriptor.child-class class-name="com.ibm.cics.example.Epde_epde__item"
 */

@SuppressWarnings( {"rawtypes", "unchecked"} )

public class EPDEv1 implements javax.resource.cci.Record,
		javax.resource.cci.Streamable, com.ibm.etools.marshall.RecordBytes {
	private static final long serialVersionUID = 30590909676370028L;
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
		bufferSize_ = 67944;
		initializedBuffer_ = new byte[bufferSize_];
		short epde__itemcountInitialValue = (short) 999;
		MarshallIntegerUtils.marshallTwoByteIntegerIntoBuffer(
				epde__itemcountInitialValue, initializedBuffer_, 10, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		String epde__strucidInitialValue = " ";
		MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
				epde__strucidInitialValue, initializedBuffer_, 0, "IBM-037", 4,
				MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		Epde_epde__item epde__item = new Epde_epde__item();
		byte[] epde__itemBytes = epde__item.getBytes();
		for (int index1 = 0; index1 < 999; index1++) {
			int bytesToCopy = epde__itemBytes.length;
			System.arraycopy(epde__itemBytes, 0, initializedBuffer_,
					12 + (68 * index1), bytesToCopy);
		}
	}

	/**
	 * constructor
	 * @generated
	 */
	public EPDEv1() {
		initialize();
	}

	/**
	 * constructor
	 * @generated
	 */
	public EPDEv1(java.util.HashMap valFieldNameMap) {
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
			valFieldNameMap.put("val(1,1,4)", "epde__itemcount");
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
		if ((buffer_ == null) || (valFieldNameMap_ == null))
			return (67944);
		String formula = "add(12,mpy(68,val(1,1,4)))";
		int size = evaluateFormula(formula, valFieldNameMap_);
		return (size);
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
	 * @type-descriptor.restriction maxLength="4"
	 * @type-descriptor.initial-value kind="SPACE"
	 * @type-descriptor.level88 name="epde__struc__id" value="EPDE"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="0" size="4"
	 * @type-descriptor.string-td characterSize="1" lengthEncoding="fixedLength" paddingCharacter=" " prefixLength="0"
	 */
	public String getEpde__strucid() {
		String epde__strucid = null;
		epde__strucid = MarshallStringUtils
				.unmarshallFixedLengthStringFromBuffer(buffer_, 0, "IBM-037", 4);
		return (epde__strucid);
	}

	/**
	 * @generated
	 */
	public void setEpde__strucid(String epde__strucid) {
		if (epde__strucid != null) {
			if (epde__strucid.length() > 4)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0124E, epde__strucid,
								"4", "epde__strucid"));
			MarshallStringUtils.marshallFixedLengthStringIntoBuffer(
					epde__strucid, buffer_, 0, "IBM-037", 4,
					MarshallStringUtils.STRING_JUSTIFICATION_LEFT, " ");
		}
	}

	/**
	 * @generated
	 */
	public String getEpde__struc__id() {
		return ("EPDE");
	}

	/**
	 * @generated
	 */
	public boolean isEpde__struc__id(String epde__struc__id) {
		return ("EPDE".equals(epde__struc__id));
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-99999999" upperBound="99999999"
	 * @type-descriptor.level88 name="epde__current__version" value="1"
	 * @type-descriptor.level88 name="epde__version__1" value="1"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="4" offset="4" size="4"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public int getEpde__version() {
		int epde__version = 0;
		epde__version = MarshallIntegerUtils
				.unmarshallFourByteIntegerFromBuffer(buffer_, 4, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__version);
	}

	/**
	 * @generated
	 */
	public void setEpde__version(int epde__version) {
		if ((epde__version < -99999999) || (epde__version > 99999999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Integer.toString(epde__version), "epde__version",
							"-99999999", "99999999"));
		MarshallIntegerUtils.marshallFourByteIntegerIntoBuffer(epde__version,
				buffer_, 4, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 */
	public int getEpde__current__version() {
		return (1);
	}

	/**
	 * @generated
	 */
	public boolean isEpde__current__version(int epde__current__version) {
		return (epde__current__version == 1);
	}

	/**
	 * @generated
	 */
	public int getEpde__version__1() {
		return (1);
	}

	/**
	 * @generated
	 */
	public boolean isEpde__version__1(int epde__version__1) {
		return (epde__version__1 == 1);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-9999" upperBound="9999"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="2" offset="8" size="2"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public short getEpde__itemlength() {
		short epde__itemlength = 0;
		epde__itemlength = MarshallIntegerUtils
				.unmarshallTwoByteIntegerFromBuffer(buffer_, 8, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__itemlength);
	}

	/**
	 * @generated
	 */
	public void setEpde__itemlength(short epde__itemlength) {
		if ((epde__itemlength < -9999) || (epde__itemlength > 9999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Short.toString(epde__itemlength),
							"epde__itemlength", "-9999", "9999"));
		MarshallIntegerUtils.marshallTwoByteIntegerIntoBuffer(epde__itemlength,
				buffer_, 8, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
	}

	/**
	 * @generated
	 * @type-descriptor.restriction lowerBound="-9999" upperBound="9999"
	 * @type-descriptor.simple-instance-td accessor="readWrite" contentSize="2" offset="10" size="2"
	 * @type-descriptor.integer-td signCoding="twosComplement"
	 */
	public short getEpde__itemcount() {
		short epde__itemcount = 0;
		epde__itemcount = MarshallIntegerUtils
				.unmarshallTwoByteIntegerFromBuffer(buffer_, 10, true,
						MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		return (epde__itemcount);
	}

	/**
	 * @generated
	 */
	public void setEpde__itemcount(short epde__itemcount) {
		if ((epde__itemcount < -9999) || (epde__itemcount > 999))
			throw new IllegalArgumentException(MarshallResource.instance()
					.getString(MarshallResource.IWAA0127E,
							Short.toString(epde__itemcount), "epde__itemcount",
							"-9999", "999"));
		MarshallIntegerUtils.marshallTwoByteIntegerIntoBuffer(epde__itemcount,
				buffer_, 10, true,
				MarshallIntegerUtils.SIGN_CODING_TWOS_COMPLEMENT);
		if (buffer_.length < bufferSize_) {
			byte[] newBuffer = new byte[bufferSize_];
			System.arraycopy(buffer_, 0, newBuffer, 0, buffer_.length);
			buffer_ = newBuffer;
		}
	}

	/**
	 * @generated
	 * @type-descriptor.depending-on dependentField="epde__itemcount"
	 * @type-descriptor.aggregate-instance-td-method accessor="readWrite" contentSize="mpy(68,val(1,1,4))" offset="12" size="67932"
	 * @type-descriptor.array-td lowerBound="0" stride="68" upperBound="val(1,1,4)"
	 */
	public Epde_epde__item[] getEpde__item() {
		int baseOffset = 12;
		int stride = 68;
		int upperBound = evaluateFormula("val(1,1,4)", valFieldNameMap_);
		Epde_epde__item[] epde__item = new Epde_epde__item[upperBound];
		java.util.HashMap evaluatedMap = evaluateMap(valFieldNameMap_);
		for (int i = 0; i < upperBound; i++) {
			int offset = baseOffset + (stride * i);
			int bytesToCopy = stride;
			byte[] subBuffer = new byte[stride];
			System.arraycopy(buffer_, offset, subBuffer, 0, bytesToCopy);
			Epde_epde__item subRecord = new Epde_epde__item(evaluatedMap);
			subRecord.setBytes(subBuffer);
			epde__item[i] = subRecord;
		}
		return (epde__item);
	}

	/**
	 * @generated
	 */
	public void setEpde__item(Epde_epde__item[] epde__item) {
		int baseOffset = 12;
		int stride = 68;
		int upperBound = evaluateFormula("val(1,1,4)", valFieldNameMap_);
		if (epde__item != null) {
			if (epde__item.length < 0)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0122E, "epde__item",
								"0"));
			if (epde__item.length > upperBound)
				throw new IllegalArgumentException(MarshallResource.instance()
						.getString(MarshallResource.IWAA0123E, "epde__item",
								Integer.toString(upperBound)));
			int numElems = Math.min(epde__item.length, upperBound);
			for (int i = 0; i < numElems; i++) {
				if (epde__item[i] == null)
					continue;
				int offset = baseOffset + (stride * i);
				byte[] subBuffer = epde__item[i].getBytes();
				int bytesToCopy = subBuffer.length;
				System.arraycopy(subBuffer, 0, buffer_, offset, bytesToCopy);
			}
		}
	}

	/**
	 * @generated
	 */
	public Epde_epde__item getEpde__item(int index) {
		int upperBound = evaluateFormula("val(1,1,4)", valFieldNameMap_);
		if ((index < 0) || (index > upperBound - 1))
			throw new ArrayIndexOutOfBoundsException(index);
		int baseOffset = 12;
		int stride = 68;
		int offset = baseOffset + (stride * index);
		int bytesToCopy = stride;
		byte[] subBuffer = new byte[stride];
		System.arraycopy(buffer_, offset, subBuffer, 0, bytesToCopy);
		Epde_epde__item epde__item = new Epde_epde__item(
				evaluateMap(valFieldNameMap_));
		epde__item.setBytes(subBuffer);
		return (epde__item);
	}

	/**
	 * @generated
	 */
	public void setEpde__item(int index, Epde_epde__item epde__item) {
		int upperBound = evaluateFormula("val(1,1,4)", valFieldNameMap_);
		if ((index < 0) || (index > upperBound - 1))
			throw new ArrayIndexOutOfBoundsException(index);
		int baseOffset = 12;
		int stride = 68;
		int offset = baseOffset + (stride * index);
		byte[] subBuffer = epde__item.getBytes();
		int bytesToCopy = subBuffer.length;
		System.arraycopy(subBuffer, 0, buffer_, offset, bytesToCopy);
	}

}