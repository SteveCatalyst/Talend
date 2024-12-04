
package test_sb.flatfile_0_1;

import routines.Numeric;
import routines.DataOperation;
import routines.TalendDataGenerator;
import routines.TalendStringUtil;
import routines.TalendString;
import routines.MDM;
import routines.StringHandling;
import routines.Relational;
import routines.TalendDate;
import routines.Mathematical;
import routines.SQLike;
import routines.system.*;
import routines.system.api.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Comparator;

@SuppressWarnings("unused")

/**
 * Job: Flatfile Purpose: <br>
 * Description: Test for flatfile ingestion <br>
 * 
 * @author Bates, Steve
 * @version 8.0.1.20241016_1624-patch
 * @status
 */
public class Flatfile implements TalendJob {
	static {
		System.setProperty("TalendJob.log", "Flatfile.log");
	}

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Flatfile.class);

	protected static void logIgnoredError(String message, Throwable cause) {
		log.error(message, cause);

	}

	public final Object obj = new Object();

	// for transmiting parameters purpose
	private Object valueObject = null;

	public Object getValueObject() {
		return this.valueObject;
	}

	public void setValueObject(Object valueObject) {
		this.valueObject = valueObject;
	}

	private final static String defaultCharset = java.nio.charset.Charset.defaultCharset().name();

	private final static String utf8Charset = "UTF-8";

	public static String taskExecutionId = null;

	public static String jobExecutionId = java.util.UUID.randomUUID().toString();;

	// contains type for every context property
	public class PropertiesWithType extends java.util.Properties {
		private static final long serialVersionUID = 1L;
		private java.util.Map<String, String> propertyTypes = new java.util.HashMap<>();

		public PropertiesWithType(java.util.Properties properties) {
			super(properties);
		}

		public PropertiesWithType() {
			super();
		}

		public void setContextType(String key, String type) {
			propertyTypes.put(key, type);
		}

		public String getContextType(String key) {
			return propertyTypes.get(key);
		}
	}

	// create and load default properties
	private java.util.Properties defaultProps = new java.util.Properties();

	// create application properties with default
	public class ContextProperties extends PropertiesWithType {

		private static final long serialVersionUID = 1L;

		public ContextProperties(java.util.Properties properties) {
			super(properties);
		}

		public ContextProperties() {
			super();
		}

		public void synchronizeContext() {

		}

		// if the stored or passed value is "<TALEND_NULL>" string, it mean null
		public String getStringValue(String key) {
			String origin_value = this.getProperty(key);
			if (NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY.equals(origin_value)) {
				return null;
			}
			return origin_value;
		}

	}

	protected ContextProperties context = new ContextProperties(); // will be instanciated by MS.

	public ContextProperties getContext() {
		return this.context;
	}

	protected java.util.Map<String, String> defaultProperties = new java.util.HashMap<String, String>();
	protected java.util.Map<String, String> additionalProperties = new java.util.HashMap<String, String>();

	public java.util.Map<String, String> getDefaultProperties() {
		return this.defaultProperties;
	}

	public java.util.Map<String, String> getAdditionalProperties() {
		return this.additionalProperties;
	}

	private final String jobVersion = "0.1";
	private final String jobName = "Flatfile";
	private final String projectName = "TEST_SB";
	public Integer errorCode = null;
	private String currentComponent = "";
	public static boolean isStandaloneMS = Boolean.valueOf("false");

	private void s(final String component) {
		try {
			org.talend.metrics.DataReadTracker.setCurrentComponent(jobName, component);
		} catch (Exception | NoClassDefFoundError e) {
			// ignore
		}
	}

	private void mdc(final String subJobName, final String subJobPidPrefix) {
		mdcInfo.forEach(org.slf4j.MDC::put);
		org.slf4j.MDC.put("_subJobName", subJobName);
		org.slf4j.MDC.put("_subJobPid", subJobPidPrefix + subJobPidCounter.getAndIncrement());
	}

	private void sh(final String componentId) {
		ok_Hash.put(componentId, false);
		start_Hash.put(componentId, System.currentTimeMillis());
	}

	{
		s("none");
	}

	private String cLabel = null;

	private final java.util.Map<String, Object> globalMap = new java.util.HashMap<String, Object>();
	private final static java.util.Map<String, Object> junitGlobalMap = new java.util.HashMap<String, Object>();

	private final java.util.Map<String, Long> start_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Long> end_Hash = new java.util.HashMap<String, Long>();
	private final java.util.Map<String, Boolean> ok_Hash = new java.util.HashMap<String, Boolean>();
	public final java.util.List<String[]> globalBuffer = new java.util.ArrayList<String[]>();

	private final JobStructureCatcherUtils talendJobLog = new JobStructureCatcherUtils(jobName,
			"_n4_hkKwHEe-23YFR61FRHQ", "0.1");
	private org.talend.job.audit.JobAuditLogger auditLogger_talendJobLog = null;

	private RunStat runStat = new RunStat(talendJobLog, System.getProperty("audit.interval"));

	// OSGi DataSource
	private final static String KEY_DB_DATASOURCES = "KEY_DB_DATASOURCES";

	private final static String KEY_DB_DATASOURCES_RAW = "KEY_DB_DATASOURCES_RAW";

	public void setDataSources(java.util.Map<String, javax.sql.DataSource> dataSources) {
		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		for (java.util.Map.Entry<String, javax.sql.DataSource> dataSourceEntry : dataSources.entrySet()) {
			talendDataSources.put(dataSourceEntry.getKey(),
					new routines.system.TalendDataSource(dataSourceEntry.getValue()));
		}
		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	public void setDataSourceReferences(List serviceReferences) throws Exception {

		java.util.Map<String, routines.system.TalendDataSource> talendDataSources = new java.util.HashMap<String, routines.system.TalendDataSource>();
		java.util.Map<String, javax.sql.DataSource> dataSources = new java.util.HashMap<String, javax.sql.DataSource>();

		for (java.util.Map.Entry<String, javax.sql.DataSource> entry : BundleUtils
				.getServices(serviceReferences, javax.sql.DataSource.class).entrySet()) {
			dataSources.put(entry.getKey(), entry.getValue());
			talendDataSources.put(entry.getKey(), new routines.system.TalendDataSource(entry.getValue()));
		}

		globalMap.put(KEY_DB_DATASOURCES, talendDataSources);
		globalMap.put(KEY_DB_DATASOURCES_RAW, new java.util.HashMap<String, javax.sql.DataSource>(dataSources));
	}

	private final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	private final java.io.PrintStream errorMessagePS = new java.io.PrintStream(new java.io.BufferedOutputStream(baos));

	public String getExceptionStackTrace() {
		if ("failure".equals(this.getStatus())) {
			errorMessagePS.flush();
			return baos.toString();
		}
		return null;
	}

	private Exception exception;

	public Exception getException() {
		if ("failure".equals(this.getStatus())) {
			return this.exception;
		}
		return null;
	}

	private class TalendException extends Exception {

		private static final long serialVersionUID = 1L;

		private java.util.Map<String, Object> globalMap = null;
		private Exception e = null;

		private String currentComponent = null;
		private String cLabel = null;

		private String virtualComponentName = null;

		public void setVirtualComponentName(String virtualComponentName) {
			this.virtualComponentName = virtualComponentName;
		}

		private TalendException(Exception e, String errorComponent, final java.util.Map<String, Object> globalMap) {
			this.currentComponent = errorComponent;
			this.globalMap = globalMap;
			this.e = e;
		}

		private TalendException(Exception e, String errorComponent, String errorComponentLabel,
				final java.util.Map<String, Object> globalMap) {
			this(e, errorComponent, globalMap);
			this.cLabel = errorComponentLabel;
		}

		public Exception getException() {
			return this.e;
		}

		public String getCurrentComponent() {
			return this.currentComponent;
		}

		public String getExceptionCauseMessage(Exception e) {
			Throwable cause = e;
			String message = null;
			int i = 10;
			while (null != cause && 0 < i--) {
				message = cause.getMessage();
				if (null == message) {
					cause = cause.getCause();
				} else {
					break;
				}
			}
			if (null == message) {
				message = e.getClass().getName();
			}
			return message;
		}

		@Override
		public void printStackTrace() {
			if (!(e instanceof TalendException || e instanceof TDieException)) {
				if (virtualComponentName != null && currentComponent.indexOf(virtualComponentName + "_") == 0) {
					globalMap.put(virtualComponentName + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				}
				globalMap.put(currentComponent + "_ERROR_MESSAGE", getExceptionCauseMessage(e));
				System.err.println("Exception in component " + currentComponent + " (" + jobName + ")");
			}
			if (!(e instanceof TDieException)) {
				if (e instanceof TalendException) {
					e.printStackTrace();
				} else {
					e.printStackTrace();
					e.printStackTrace(errorMessagePS);
					Flatfile.this.exception = e;
				}
			}
			if (!(e instanceof TalendException)) {
				try {
					for (java.lang.reflect.Method m : this.getClass().getEnclosingClass().getMethods()) {
						if (m.getName().compareTo(currentComponent + "_error") == 0) {
							m.invoke(Flatfile.this, new Object[] { e, currentComponent, globalMap });
							break;
						}
					}

					if (!(e instanceof TDieException)) {
						if (enableLogStash) {
							talendJobLog.addJobExceptionMessage(currentComponent, cLabel, null, e);
							talendJobLogProcess(globalMap);
						}
					}
				} catch (Exception e) {
					this.e.printStackTrace();
				}
			}
		}
	}

	public void tFileInputDelimited_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tDBOutput_1_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		tFileInputDelimited_1_onSubJobError(exception, errorComponent, globalMap);
	}

	public void talendJobLog_error(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		end_Hash.put(errorComponent, System.currentTimeMillis());

		status = "failure";

		talendJobLog_onSubJobError(exception, errorComponent, globalMap);
	}

	public void tFileInputDelimited_1_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public void talendJobLog_onSubJobError(Exception exception, String errorComponent,
			final java.util.Map<String, Object> globalMap) throws TalendException {

		resumeUtil.addLog("SYSTEM_LOG", "NODE:" + errorComponent, "", Thread.currentThread().getId() + "", "FATAL", "",
				exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");

	}

	public static class row1Struct implements routines.system.IPersistableRow<row1Struct> {
		final static byte[] commonByteArrayLock_TEST_SB_Flatfile = new byte[0];
		static byte[] commonByteArray_TEST_SB_Flatfile = new byte[0];

		public Integer ORDERNUMBER;

		public Integer getORDERNUMBER() {
			return this.ORDERNUMBER;
		}

		public Boolean ORDERNUMBERIsNullable() {
			return true;
		}

		public Boolean ORDERNUMBERIsKey() {
			return false;
		}

		public Integer ORDERNUMBERLength() {
			return 5;
		}

		public Integer ORDERNUMBERPrecision() {
			return 0;
		}

		public String ORDERNUMBERDefault() {

			return null;

		}

		public String ORDERNUMBERComment() {

			return "";

		}

		public String ORDERNUMBERPattern() {

			return "dd-MM-yyyy";

		}

		public String ORDERNUMBEROriginalDbColumnName() {

			return "ORDERNUMBER";

		}

		public Integer QUANTITYORDERED;

		public Integer getQUANTITYORDERED() {
			return this.QUANTITYORDERED;
		}

		public Boolean QUANTITYORDEREDIsNullable() {
			return true;
		}

		public Boolean QUANTITYORDEREDIsKey() {
			return false;
		}

		public Integer QUANTITYORDEREDLength() {
			return 2;
		}

		public Integer QUANTITYORDEREDPrecision() {
			return 0;
		}

		public String QUANTITYORDEREDDefault() {

			return null;

		}

		public String QUANTITYORDEREDComment() {

			return "";

		}

		public String QUANTITYORDEREDPattern() {

			return "dd-MM-yyyy";

		}

		public String QUANTITYORDEREDOriginalDbColumnName() {

			return "QUANTITYORDERED";

		}

		public Float PRICEEACH;

		public Float getPRICEEACH() {
			return this.PRICEEACH;
		}

		public Boolean PRICEEACHIsNullable() {
			return true;
		}

		public Boolean PRICEEACHIsKey() {
			return false;
		}

		public Integer PRICEEACHLength() {
			return 5;
		}

		public Integer PRICEEACHPrecision() {
			return 3;
		}

		public String PRICEEACHDefault() {

			return null;

		}

		public String PRICEEACHComment() {

			return "";

		}

		public String PRICEEACHPattern() {

			return "dd-MM-yyyy";

		}

		public String PRICEEACHOriginalDbColumnName() {

			return "PRICEEACH";

		}

		public Integer ORDERLINENUMBER;

		public Integer getORDERLINENUMBER() {
			return this.ORDERLINENUMBER;
		}

		public Boolean ORDERLINENUMBERIsNullable() {
			return true;
		}

		public Boolean ORDERLINENUMBERIsKey() {
			return false;
		}

		public Integer ORDERLINENUMBERLength() {
			return 2;
		}

		public Integer ORDERLINENUMBERPrecision() {
			return 0;
		}

		public String ORDERLINENUMBERDefault() {

			return null;

		}

		public String ORDERLINENUMBERComment() {

			return "";

		}

		public String ORDERLINENUMBERPattern() {

			return "dd-MM-yyyy";

		}

		public String ORDERLINENUMBEROriginalDbColumnName() {

			return "ORDERLINENUMBER";

		}

		public Float SALES;

		public Float getSALES() {
			return this.SALES;
		}

		public Boolean SALESIsNullable() {
			return true;
		}

		public Boolean SALESIsKey() {
			return false;
		}

		public Integer SALESLength() {
			return 7;
		}

		public Integer SALESPrecision() {
			return 3;
		}

		public String SALESDefault() {

			return null;

		}

		public String SALESComment() {

			return "";

		}

		public String SALESPattern() {

			return "dd-MM-yyyy";

		}

		public String SALESOriginalDbColumnName() {

			return "SALES";

		}

		public java.util.Date ORDERDATE;

		public java.util.Date getORDERDATE() {
			return this.ORDERDATE;
		}

		public Boolean ORDERDATEIsNullable() {
			return true;
		}

		public Boolean ORDERDATEIsKey() {
			return false;
		}

		public Integer ORDERDATELength() {
			return 40;
		}

		public Integer ORDERDATEPrecision() {
			return 0;
		}

		public String ORDERDATEDefault() {

			return null;

		}

		public String ORDERDATEComment() {

			return "";

		}

		public String ORDERDATEPattern() {

			return "d/M/yyyy h:mm";

		}

		public String ORDERDATEOriginalDbColumnName() {

			return "ORDERDATE";

		}

		public String STATUS;

		public String getSTATUS() {
			return this.STATUS;
		}

		public Boolean STATUSIsNullable() {
			return true;
		}

		public Boolean STATUSIsKey() {
			return false;
		}

		public Integer STATUSLength() {
			return 20;
		}

		public Integer STATUSPrecision() {
			return 0;
		}

		public String STATUSDefault() {

			return null;

		}

		public String STATUSComment() {

			return "";

		}

		public String STATUSPattern() {

			return "dd-MM-yyyy";

		}

		public String STATUSOriginalDbColumnName() {

			return "STATUS";

		}

		public Integer QTR_ID;

		public Integer getQTR_ID() {
			return this.QTR_ID;
		}

		public Boolean QTR_IDIsNullable() {
			return true;
		}

		public Boolean QTR_IDIsKey() {
			return false;
		}

		public Integer QTR_IDLength() {
			return 1;
		}

		public Integer QTR_IDPrecision() {
			return 0;
		}

		public String QTR_IDDefault() {

			return null;

		}

		public String QTR_IDComment() {

			return "";

		}

		public String QTR_IDPattern() {

			return "dd-MM-yyyy";

		}

		public String QTR_IDOriginalDbColumnName() {

			return "QTR_ID";

		}

		public Integer MONTH_ID;

		public Integer getMONTH_ID() {
			return this.MONTH_ID;
		}

		public Boolean MONTH_IDIsNullable() {
			return true;
		}

		public Boolean MONTH_IDIsKey() {
			return false;
		}

		public Integer MONTH_IDLength() {
			return 2;
		}

		public Integer MONTH_IDPrecision() {
			return 0;
		}

		public String MONTH_IDDefault() {

			return null;

		}

		public String MONTH_IDComment() {

			return "";

		}

		public String MONTH_IDPattern() {

			return "dd-MM-yyyy";

		}

		public String MONTH_IDOriginalDbColumnName() {

			return "MONTH_ID";

		}

		public Integer YEAR_ID;

		public Integer getYEAR_ID() {
			return this.YEAR_ID;
		}

		public Boolean YEAR_IDIsNullable() {
			return true;
		}

		public Boolean YEAR_IDIsKey() {
			return false;
		}

		public Integer YEAR_IDLength() {
			return 4;
		}

		public Integer YEAR_IDPrecision() {
			return 0;
		}

		public String YEAR_IDDefault() {

			return null;

		}

		public String YEAR_IDComment() {

			return "";

		}

		public String YEAR_IDPattern() {

			return "dd-MM-yyyy";

		}

		public String YEAR_IDOriginalDbColumnName() {

			return "YEAR_ID";

		}

		public String PRODUCTLINE;

		public String getPRODUCTLINE() {
			return this.PRODUCTLINE;
		}

		public Boolean PRODUCTLINEIsNullable() {
			return true;
		}

		public Boolean PRODUCTLINEIsKey() {
			return false;
		}

		public Integer PRODUCTLINELength() {
			return 30;
		}

		public Integer PRODUCTLINEPrecision() {
			return 0;
		}

		public String PRODUCTLINEDefault() {

			return null;

		}

		public String PRODUCTLINEComment() {

			return "";

		}

		public String PRODUCTLINEPattern() {

			return "dd-MM-yyyy";

		}

		public String PRODUCTLINEOriginalDbColumnName() {

			return "PRODUCTLINE";

		}

		public Integer MSRP;

		public Integer getMSRP() {
			return this.MSRP;
		}

		public Boolean MSRPIsNullable() {
			return true;
		}

		public Boolean MSRPIsKey() {
			return false;
		}

		public Integer MSRPLength() {
			return 3;
		}

		public Integer MSRPPrecision() {
			return 0;
		}

		public String MSRPDefault() {

			return null;

		}

		public String MSRPComment() {

			return "";

		}

		public String MSRPPattern() {

			return "dd-MM-yyyy";

		}

		public String MSRPOriginalDbColumnName() {

			return "MSRP";

		}

		public String PRODUCTCODE;

		public String getPRODUCTCODE() {
			return this.PRODUCTCODE;
		}

		public Boolean PRODUCTCODEIsNullable() {
			return true;
		}

		public Boolean PRODUCTCODEIsKey() {
			return false;
		}

		public Integer PRODUCTCODELength() {
			return 20;
		}

		public Integer PRODUCTCODEPrecision() {
			return 0;
		}

		public String PRODUCTCODEDefault() {

			return null;

		}

		public String PRODUCTCODEComment() {

			return "";

		}

		public String PRODUCTCODEPattern() {

			return "dd-MM-yyyy";

		}

		public String PRODUCTCODEOriginalDbColumnName() {

			return "PRODUCTCODE";

		}

		public String CUSTOMERNAME;

		public String getCUSTOMERNAME() {
			return this.CUSTOMERNAME;
		}

		public Boolean CUSTOMERNAMEIsNullable() {
			return true;
		}

		public Boolean CUSTOMERNAMEIsKey() {
			return false;
		}

		public Integer CUSTOMERNAMELength() {
			return 50;
		}

		public Integer CUSTOMERNAMEPrecision() {
			return 0;
		}

		public String CUSTOMERNAMEDefault() {

			return null;

		}

		public String CUSTOMERNAMEComment() {

			return "";

		}

		public String CUSTOMERNAMEPattern() {

			return "dd-MM-yyyy";

		}

		public String CUSTOMERNAMEOriginalDbColumnName() {

			return "CUSTOMERNAME";

		}

		public String PHONE;

		public String getPHONE() {
			return this.PHONE;
		}

		public Boolean PHONEIsNullable() {
			return true;
		}

		public Boolean PHONEIsKey() {
			return false;
		}

		public Integer PHONELength() {
			return 20;
		}

		public Integer PHONEPrecision() {
			return 0;
		}

		public String PHONEDefault() {

			return null;

		}

		public String PHONEComment() {

			return "";

		}

		public String PHONEPattern() {

			return "dd-MM-yyyy";

		}

		public String PHONEOriginalDbColumnName() {

			return "PHONE";

		}

		public String ADDRESSLINE1;

		public String getADDRESSLINE1() {
			return this.ADDRESSLINE1;
		}

		public Boolean ADDRESSLINE1IsNullable() {
			return true;
		}

		public Boolean ADDRESSLINE1IsKey() {
			return false;
		}

		public Integer ADDRESSLINE1Length() {
			return 50;
		}

		public Integer ADDRESSLINE1Precision() {
			return 0;
		}

		public String ADDRESSLINE1Default() {

			return null;

		}

		public String ADDRESSLINE1Comment() {

			return "";

		}

		public String ADDRESSLINE1Pattern() {

			return "dd-MM-yyyy";

		}

		public String ADDRESSLINE1OriginalDbColumnName() {

			return "ADDRESSLINE1";

		}

		public String ADDRESSLINE2;

		public String getADDRESSLINE2() {
			return this.ADDRESSLINE2;
		}

		public Boolean ADDRESSLINE2IsNullable() {
			return true;
		}

		public Boolean ADDRESSLINE2IsKey() {
			return false;
		}

		public Integer ADDRESSLINE2Length() {
			return 20;
		}

		public Integer ADDRESSLINE2Precision() {
			return 0;
		}

		public String ADDRESSLINE2Default() {

			return null;

		}

		public String ADDRESSLINE2Comment() {

			return "";

		}

		public String ADDRESSLINE2Pattern() {

			return "dd-MM-yyyy";

		}

		public String ADDRESSLINE2OriginalDbColumnName() {

			return "ADDRESSLINE2";

		}

		public String CITY;

		public String getCITY() {
			return this.CITY;
		}

		public Boolean CITYIsNullable() {
			return true;
		}

		public Boolean CITYIsKey() {
			return false;
		}

		public Integer CITYLength() {
			return 30;
		}

		public Integer CITYPrecision() {
			return 0;
		}

		public String CITYDefault() {

			return null;

		}

		public String CITYComment() {

			return "";

		}

		public String CITYPattern() {

			return "dd-MM-yyyy";

		}

		public String CITYOriginalDbColumnName() {

			return "CITY";

		}

		public String STATE;

		public String getSTATE() {
			return this.STATE;
		}

		public Boolean STATEIsNullable() {
			return true;
		}

		public Boolean STATEIsKey() {
			return false;
		}

		public Integer STATELength() {
			return 20;
		}

		public Integer STATEPrecision() {
			return 0;
		}

		public String STATEDefault() {

			return null;

		}

		public String STATEComment() {

			return "";

		}

		public String STATEPattern() {

			return "dd-MM-yyyy";

		}

		public String STATEOriginalDbColumnName() {

			return "STATE";

		}

		public String POSTALCODE;

		public String getPOSTALCODE() {
			return this.POSTALCODE;
		}

		public Boolean POSTALCODEIsNullable() {
			return true;
		}

		public Boolean POSTALCODEIsKey() {
			return false;
		}

		public Integer POSTALCODELength() {
			return 20;
		}

		public Integer POSTALCODEPrecision() {
			return 0;
		}

		public String POSTALCODEDefault() {

			return null;

		}

		public String POSTALCODEComment() {

			return "";

		}

		public String POSTALCODEPattern() {

			return "dd-MM-yyyy";

		}

		public String POSTALCODEOriginalDbColumnName() {

			return "POSTALCODE";

		}

		public String COUNTRY;

		public String getCOUNTRY() {
			return this.COUNTRY;
		}

		public Boolean COUNTRYIsNullable() {
			return true;
		}

		public Boolean COUNTRYIsKey() {
			return false;
		}

		public Integer COUNTRYLength() {
			return 20;
		}

		public Integer COUNTRYPrecision() {
			return 0;
		}

		public String COUNTRYDefault() {

			return null;

		}

		public String COUNTRYComment() {

			return "";

		}

		public String COUNTRYPattern() {

			return "dd-MM-yyyy";

		}

		public String COUNTRYOriginalDbColumnName() {

			return "COUNTRY";

		}

		public String TERRITORY;

		public String getTERRITORY() {
			return this.TERRITORY;
		}

		public Boolean TERRITORYIsNullable() {
			return true;
		}

		public Boolean TERRITORYIsKey() {
			return false;
		}

		public Integer TERRITORYLength() {
			return 20;
		}

		public Integer TERRITORYPrecision() {
			return 0;
		}

		public String TERRITORYDefault() {

			return null;

		}

		public String TERRITORYComment() {

			return "";

		}

		public String TERRITORYPattern() {

			return "dd-MM-yyyy";

		}

		public String TERRITORYOriginalDbColumnName() {

			return "TERRITORY";

		}

		public String CONTACTLASTNAME;

		public String getCONTACTLASTNAME() {
			return this.CONTACTLASTNAME;
		}

		public Boolean CONTACTLASTNAMEIsNullable() {
			return true;
		}

		public Boolean CONTACTLASTNAMEIsKey() {
			return false;
		}

		public Integer CONTACTLASTNAMELength() {
			return 20;
		}

		public Integer CONTACTLASTNAMEPrecision() {
			return 0;
		}

		public String CONTACTLASTNAMEDefault() {

			return null;

		}

		public String CONTACTLASTNAMEComment() {

			return "";

		}

		public String CONTACTLASTNAMEPattern() {

			return "dd-MM-yyyy";

		}

		public String CONTACTLASTNAMEOriginalDbColumnName() {

			return "CONTACTLASTNAME";

		}

		public String CONTACTFIRSTNAME;

		public String getCONTACTFIRSTNAME() {
			return this.CONTACTFIRSTNAME;
		}

		public Boolean CONTACTFIRSTNAMEIsNullable() {
			return true;
		}

		public Boolean CONTACTFIRSTNAMEIsKey() {
			return false;
		}

		public Integer CONTACTFIRSTNAMELength() {
			return 20;
		}

		public Integer CONTACTFIRSTNAMEPrecision() {
			return 0;
		}

		public String CONTACTFIRSTNAMEDefault() {

			return null;

		}

		public String CONTACTFIRSTNAMEComment() {

			return "";

		}

		public String CONTACTFIRSTNAMEPattern() {

			return "dd-MM-yyyy";

		}

		public String CONTACTFIRSTNAMEOriginalDbColumnName() {

			return "CONTACTFIRSTNAME";

		}

		public String DEALSIZE;

		public String getDEALSIZE() {
			return this.DEALSIZE;
		}

		public Boolean DEALSIZEIsNullable() {
			return true;
		}

		public Boolean DEALSIZEIsKey() {
			return false;
		}

		public Integer DEALSIZELength() {
			return 20;
		}

		public Integer DEALSIZEPrecision() {
			return 0;
		}

		public String DEALSIZEDefault() {

			return null;

		}

		public String DEALSIZEComment() {

			return "";

		}

		public String DEALSIZEPattern() {

			return "dd-MM-yyyy";

		}

		public String DEALSIZEOriginalDbColumnName() {

			return "DEALSIZE";

		}

		private Integer readInteger(ObjectInputStream dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private Integer readInteger(org.jboss.marshalling.Unmarshaller dis) throws IOException {
			Integer intReturn;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				intReturn = null;
			} else {
				intReturn = dis.readInt();
			}
			return intReturn;
		}

		private void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException {
			if (intNum == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeInt(intNum);
			}
		}

		private void writeInteger(Integer intNum, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (intNum == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeInt(intNum);
			}
		}

		private java.util.Date readDate(ObjectInputStream dis) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = dis.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(dis.readLong());
			}
			return dateReturn;
		}

		private java.util.Date readDate(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			java.util.Date dateReturn = null;
			int length = 0;
			length = unmarshaller.readByte();
			if (length == -1) {
				dateReturn = null;
			} else {
				dateReturn = new Date(unmarshaller.readLong());
			}
			return dateReturn;
		}

		private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException {
			if (date1 == null) {
				dos.writeByte(-1);
			} else {
				dos.writeByte(0);
				dos.writeLong(date1.getTime());
			}
		}

		private void writeDate(java.util.Date date1, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (date1 == null) {
				marshaller.writeByte(-1);
			} else {
				marshaller.writeByte(0);
				marshaller.writeLong(date1.getTime());
			}
		}

		private String readString(ObjectInputStream dis) throws IOException {
			String strReturn = null;
			int length = 0;
			length = dis.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TEST_SB_Flatfile.length) {
					if (length < 1024 && commonByteArray_TEST_SB_Flatfile.length == 0) {
						commonByteArray_TEST_SB_Flatfile = new byte[1024];
					} else {
						commonByteArray_TEST_SB_Flatfile = new byte[2 * length];
					}
				}
				dis.readFully(commonByteArray_TEST_SB_Flatfile, 0, length);
				strReturn = new String(commonByteArray_TEST_SB_Flatfile, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private String readString(org.jboss.marshalling.Unmarshaller unmarshaller) throws IOException {
			String strReturn = null;
			int length = 0;
			length = unmarshaller.readInt();
			if (length == -1) {
				strReturn = null;
			} else {
				if (length > commonByteArray_TEST_SB_Flatfile.length) {
					if (length < 1024 && commonByteArray_TEST_SB_Flatfile.length == 0) {
						commonByteArray_TEST_SB_Flatfile = new byte[1024];
					} else {
						commonByteArray_TEST_SB_Flatfile = new byte[2 * length];
					}
				}
				unmarshaller.readFully(commonByteArray_TEST_SB_Flatfile, 0, length);
				strReturn = new String(commonByteArray_TEST_SB_Flatfile, 0, length, utf8Charset);
			}
			return strReturn;
		}

		private void writeString(String str, ObjectOutputStream dos) throws IOException {
			if (str == null) {
				dos.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				dos.writeInt(byteArray.length);
				dos.write(byteArray);
			}
		}

		private void writeString(String str, org.jboss.marshalling.Marshaller marshaller) throws IOException {
			if (str == null) {
				marshaller.writeInt(-1);
			} else {
				byte[] byteArray = str.getBytes(utf8Charset);
				marshaller.writeInt(byteArray.length);
				marshaller.write(byteArray);
			}
		}

		public void readData(ObjectInputStream dis) {

			synchronized (commonByteArrayLock_TEST_SB_Flatfile) {

				try {

					int length = 0;

					this.ORDERNUMBER = readInteger(dis);

					this.QUANTITYORDERED = readInteger(dis);

					length = dis.readByte();
					if (length == -1) {
						this.PRICEEACH = null;
					} else {
						this.PRICEEACH = dis.readFloat();
					}

					this.ORDERLINENUMBER = readInteger(dis);

					length = dis.readByte();
					if (length == -1) {
						this.SALES = null;
					} else {
						this.SALES = dis.readFloat();
					}

					this.ORDERDATE = readDate(dis);

					this.STATUS = readString(dis);

					this.QTR_ID = readInteger(dis);

					this.MONTH_ID = readInteger(dis);

					this.YEAR_ID = readInteger(dis);

					this.PRODUCTLINE = readString(dis);

					this.MSRP = readInteger(dis);

					this.PRODUCTCODE = readString(dis);

					this.CUSTOMERNAME = readString(dis);

					this.PHONE = readString(dis);

					this.ADDRESSLINE1 = readString(dis);

					this.ADDRESSLINE2 = readString(dis);

					this.CITY = readString(dis);

					this.STATE = readString(dis);

					this.POSTALCODE = readString(dis);

					this.COUNTRY = readString(dis);

					this.TERRITORY = readString(dis);

					this.CONTACTLASTNAME = readString(dis);

					this.CONTACTFIRSTNAME = readString(dis);

					this.DEALSIZE = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void readData(org.jboss.marshalling.Unmarshaller dis) {

			synchronized (commonByteArrayLock_TEST_SB_Flatfile) {

				try {

					int length = 0;

					this.ORDERNUMBER = readInteger(dis);

					this.QUANTITYORDERED = readInteger(dis);

					length = dis.readByte();
					if (length == -1) {
						this.PRICEEACH = null;
					} else {
						this.PRICEEACH = dis.readFloat();
					}

					this.ORDERLINENUMBER = readInteger(dis);

					length = dis.readByte();
					if (length == -1) {
						this.SALES = null;
					} else {
						this.SALES = dis.readFloat();
					}

					this.ORDERDATE = readDate(dis);

					this.STATUS = readString(dis);

					this.QTR_ID = readInteger(dis);

					this.MONTH_ID = readInteger(dis);

					this.YEAR_ID = readInteger(dis);

					this.PRODUCTLINE = readString(dis);

					this.MSRP = readInteger(dis);

					this.PRODUCTCODE = readString(dis);

					this.CUSTOMERNAME = readString(dis);

					this.PHONE = readString(dis);

					this.ADDRESSLINE1 = readString(dis);

					this.ADDRESSLINE2 = readString(dis);

					this.CITY = readString(dis);

					this.STATE = readString(dis);

					this.POSTALCODE = readString(dis);

					this.COUNTRY = readString(dis);

					this.TERRITORY = readString(dis);

					this.CONTACTLASTNAME = readString(dis);

					this.CONTACTFIRSTNAME = readString(dis);

					this.DEALSIZE = readString(dis);

				} catch (IOException e) {
					throw new RuntimeException(e);

				}

			}

		}

		public void writeData(ObjectOutputStream dos) {
			try {

				// Integer

				writeInteger(this.ORDERNUMBER, dos);

				// Integer

				writeInteger(this.QUANTITYORDERED, dos);

				// Float

				if (this.PRICEEACH == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeFloat(this.PRICEEACH);
				}

				// Integer

				writeInteger(this.ORDERLINENUMBER, dos);

				// Float

				if (this.SALES == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeFloat(this.SALES);
				}

				// java.util.Date

				writeDate(this.ORDERDATE, dos);

				// String

				writeString(this.STATUS, dos);

				// Integer

				writeInteger(this.QTR_ID, dos);

				// Integer

				writeInteger(this.MONTH_ID, dos);

				// Integer

				writeInteger(this.YEAR_ID, dos);

				// String

				writeString(this.PRODUCTLINE, dos);

				// Integer

				writeInteger(this.MSRP, dos);

				// String

				writeString(this.PRODUCTCODE, dos);

				// String

				writeString(this.CUSTOMERNAME, dos);

				// String

				writeString(this.PHONE, dos);

				// String

				writeString(this.ADDRESSLINE1, dos);

				// String

				writeString(this.ADDRESSLINE2, dos);

				// String

				writeString(this.CITY, dos);

				// String

				writeString(this.STATE, dos);

				// String

				writeString(this.POSTALCODE, dos);

				// String

				writeString(this.COUNTRY, dos);

				// String

				writeString(this.TERRITORY, dos);

				// String

				writeString(this.CONTACTLASTNAME, dos);

				// String

				writeString(this.CONTACTFIRSTNAME, dos);

				// String

				writeString(this.DEALSIZE, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public void writeData(org.jboss.marshalling.Marshaller dos) {
			try {

				// Integer

				writeInteger(this.ORDERNUMBER, dos);

				// Integer

				writeInteger(this.QUANTITYORDERED, dos);

				// Float

				if (this.PRICEEACH == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeFloat(this.PRICEEACH);
				}

				// Integer

				writeInteger(this.ORDERLINENUMBER, dos);

				// Float

				if (this.SALES == null) {
					dos.writeByte(-1);
				} else {
					dos.writeByte(0);
					dos.writeFloat(this.SALES);
				}

				// java.util.Date

				writeDate(this.ORDERDATE, dos);

				// String

				writeString(this.STATUS, dos);

				// Integer

				writeInteger(this.QTR_ID, dos);

				// Integer

				writeInteger(this.MONTH_ID, dos);

				// Integer

				writeInteger(this.YEAR_ID, dos);

				// String

				writeString(this.PRODUCTLINE, dos);

				// Integer

				writeInteger(this.MSRP, dos);

				// String

				writeString(this.PRODUCTCODE, dos);

				// String

				writeString(this.CUSTOMERNAME, dos);

				// String

				writeString(this.PHONE, dos);

				// String

				writeString(this.ADDRESSLINE1, dos);

				// String

				writeString(this.ADDRESSLINE2, dos);

				// String

				writeString(this.CITY, dos);

				// String

				writeString(this.STATE, dos);

				// String

				writeString(this.POSTALCODE, dos);

				// String

				writeString(this.COUNTRY, dos);

				// String

				writeString(this.TERRITORY, dos);

				// String

				writeString(this.CONTACTLASTNAME, dos);

				// String

				writeString(this.CONTACTFIRSTNAME, dos);

				// String

				writeString(this.DEALSIZE, dos);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());
			sb.append("[");
			sb.append("ORDERNUMBER=" + String.valueOf(ORDERNUMBER));
			sb.append(",QUANTITYORDERED=" + String.valueOf(QUANTITYORDERED));
			sb.append(",PRICEEACH=" + String.valueOf(PRICEEACH));
			sb.append(",ORDERLINENUMBER=" + String.valueOf(ORDERLINENUMBER));
			sb.append(",SALES=" + String.valueOf(SALES));
			sb.append(",ORDERDATE=" + String.valueOf(ORDERDATE));
			sb.append(",STATUS=" + STATUS);
			sb.append(",QTR_ID=" + String.valueOf(QTR_ID));
			sb.append(",MONTH_ID=" + String.valueOf(MONTH_ID));
			sb.append(",YEAR_ID=" + String.valueOf(YEAR_ID));
			sb.append(",PRODUCTLINE=" + PRODUCTLINE);
			sb.append(",MSRP=" + String.valueOf(MSRP));
			sb.append(",PRODUCTCODE=" + PRODUCTCODE);
			sb.append(",CUSTOMERNAME=" + CUSTOMERNAME);
			sb.append(",PHONE=" + PHONE);
			sb.append(",ADDRESSLINE1=" + ADDRESSLINE1);
			sb.append(",ADDRESSLINE2=" + ADDRESSLINE2);
			sb.append(",CITY=" + CITY);
			sb.append(",STATE=" + STATE);
			sb.append(",POSTALCODE=" + POSTALCODE);
			sb.append(",COUNTRY=" + COUNTRY);
			sb.append(",TERRITORY=" + TERRITORY);
			sb.append(",CONTACTLASTNAME=" + CONTACTLASTNAME);
			sb.append(",CONTACTFIRSTNAME=" + CONTACTFIRSTNAME);
			sb.append(",DEALSIZE=" + DEALSIZE);
			sb.append("]");

			return sb.toString();
		}

		public String toLogString() {
			StringBuilder sb = new StringBuilder();

			if (ORDERNUMBER == null) {
				sb.append("<null>");
			} else {
				sb.append(ORDERNUMBER);
			}

			sb.append("|");

			if (QUANTITYORDERED == null) {
				sb.append("<null>");
			} else {
				sb.append(QUANTITYORDERED);
			}

			sb.append("|");

			if (PRICEEACH == null) {
				sb.append("<null>");
			} else {
				sb.append(PRICEEACH);
			}

			sb.append("|");

			if (ORDERLINENUMBER == null) {
				sb.append("<null>");
			} else {
				sb.append(ORDERLINENUMBER);
			}

			sb.append("|");

			if (SALES == null) {
				sb.append("<null>");
			} else {
				sb.append(SALES);
			}

			sb.append("|");

			if (ORDERDATE == null) {
				sb.append("<null>");
			} else {
				sb.append(ORDERDATE);
			}

			sb.append("|");

			if (STATUS == null) {
				sb.append("<null>");
			} else {
				sb.append(STATUS);
			}

			sb.append("|");

			if (QTR_ID == null) {
				sb.append("<null>");
			} else {
				sb.append(QTR_ID);
			}

			sb.append("|");

			if (MONTH_ID == null) {
				sb.append("<null>");
			} else {
				sb.append(MONTH_ID);
			}

			sb.append("|");

			if (YEAR_ID == null) {
				sb.append("<null>");
			} else {
				sb.append(YEAR_ID);
			}

			sb.append("|");

			if (PRODUCTLINE == null) {
				sb.append("<null>");
			} else {
				sb.append(PRODUCTLINE);
			}

			sb.append("|");

			if (MSRP == null) {
				sb.append("<null>");
			} else {
				sb.append(MSRP);
			}

			sb.append("|");

			if (PRODUCTCODE == null) {
				sb.append("<null>");
			} else {
				sb.append(PRODUCTCODE);
			}

			sb.append("|");

			if (CUSTOMERNAME == null) {
				sb.append("<null>");
			} else {
				sb.append(CUSTOMERNAME);
			}

			sb.append("|");

			if (PHONE == null) {
				sb.append("<null>");
			} else {
				sb.append(PHONE);
			}

			sb.append("|");

			if (ADDRESSLINE1 == null) {
				sb.append("<null>");
			} else {
				sb.append(ADDRESSLINE1);
			}

			sb.append("|");

			if (ADDRESSLINE2 == null) {
				sb.append("<null>");
			} else {
				sb.append(ADDRESSLINE2);
			}

			sb.append("|");

			if (CITY == null) {
				sb.append("<null>");
			} else {
				sb.append(CITY);
			}

			sb.append("|");

			if (STATE == null) {
				sb.append("<null>");
			} else {
				sb.append(STATE);
			}

			sb.append("|");

			if (POSTALCODE == null) {
				sb.append("<null>");
			} else {
				sb.append(POSTALCODE);
			}

			sb.append("|");

			if (COUNTRY == null) {
				sb.append("<null>");
			} else {
				sb.append(COUNTRY);
			}

			sb.append("|");

			if (TERRITORY == null) {
				sb.append("<null>");
			} else {
				sb.append(TERRITORY);
			}

			sb.append("|");

			if (CONTACTLASTNAME == null) {
				sb.append("<null>");
			} else {
				sb.append(CONTACTLASTNAME);
			}

			sb.append("|");

			if (CONTACTFIRSTNAME == null) {
				sb.append("<null>");
			} else {
				sb.append(CONTACTFIRSTNAME);
			}

			sb.append("|");

			if (DEALSIZE == null) {
				sb.append("<null>");
			} else {
				sb.append(DEALSIZE);
			}

			sb.append("|");

			return sb.toString();
		}

		/**
		 * Compare keys
		 */
		public int compareTo(row1Struct other) {

			int returnValue = -1;

			return returnValue;
		}

		private int checkNullsAndCompare(Object object1, Object object2) {
			int returnValue = 0;
			if (object1 instanceof Comparable && object2 instanceof Comparable) {
				returnValue = ((Comparable) object1).compareTo(object2);
			} else if (object1 != null && object2 != null) {
				returnValue = compareStrings(object1.toString(), object2.toString());
			} else if (object1 == null && object2 != null) {
				returnValue = 1;
			} else if (object1 != null && object2 == null) {
				returnValue = -1;
			} else {
				returnValue = 0;
			}

			return returnValue;
		}

		private int compareStrings(String string1, String string2) {
			return string1.compareTo(string2);
		}

	}

	public void tFileInputDelimited_1Process(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		mdc("tFileInputDelimited_1", "9xgs9h_");

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				row1Struct row1 = new row1Struct();

				/**
				 * [tDBOutput_1 begin ] start
				 */

				sh("tDBOutput_1");

				s(currentComponent = "tDBOutput_1");

				cLabel = "sales_sf";

				runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, 0, 0, "row1");

				int tos_count_tDBOutput_1 = 0;

				if (enableLogStash) {
					talendJobLog.addCM("tDBOutput_1", "sales_sf", "tSnowflakeOutput");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				boolean doesNodeBelongToRequest_tDBOutput_1 = 0 == 0;
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> restRequest_tDBOutput_1 = (java.util.Map<String, Object>) globalMap
						.get("restRequest");
				String currentTRestRequestOperation_tDBOutput_1 = (String) (restRequest_tDBOutput_1 != null
						? restRequest_tDBOutput_1.get("OPERATION")
						: null);

				org.talend.components.api.component.ComponentDefinition def_tDBOutput_1 = new org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputDefinition();

				org.talend.components.api.component.runtime.Writer writer_tDBOutput_1 = null;
				org.talend.components.api.component.runtime.Reader reader_tDBOutput_1 = null;

				org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties props_tDBOutput_1 = (org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties) def_tDBOutput_1
						.createRuntimeProperties();
				props_tDBOutput_1.setValue("tableAction",
						org.talend.components.common.tableaction.TableAction.TableActionEnum.DROP_IF_EXISTS_AND_CREATE);

				props_tDBOutput_1.setValue("outputAction",
						org.talend.components.snowflake.tsnowflakeoutput.TSnowflakeOutputProperties.OutputAction.INSERT);

				props_tDBOutput_1.setValue("convertColumnsAndTableToUppercase", true);

				props_tDBOutput_1.setValue("usePersonalDBType", false);

				props_tDBOutput_1.setValue("convertEmptyStringsToNull", false);

				props_tDBOutput_1.setValue("useSchemaDatePattern", false);

				props_tDBOutput_1.setValue("dieOnError", false);

				class SchemaSettingTool_tDBOutput_1_1_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"rejectOutput\",\"fields\":[{", s);

						a("\"name\":\"columnName\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"rowNumber\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"category\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"character\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"errorMessage\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"byteOffset\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"line\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"sqlState\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"},{",
								s);

						a("\"name\":\"code\",\"type\":\"string\",\"talend.isLocked\":\"false\",\"talend.field.generated\":\"true\",\"talend.field.length\":\"255\"}]}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tDBOutput_1_1_fisrt sst_tDBOutput_1_1_fisrt = new SchemaSettingTool_tDBOutput_1_1_fisrt();

				props_tDBOutput_1.schemaReject.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tDBOutput_1_1_fisrt.getSchemaValue()));

				props_tDBOutput_1.connection.setValue("loginTimeout", 15);

				props_tDBOutput_1.connection.setValue("account", "lt28176");

				props_tDBOutput_1.connection.setValue("regionID", "");

				props_tDBOutput_1.connection.setValue("useCustomRegion", false);

				props_tDBOutput_1.connection.setValue("jdbcUrlSuffix", ".eu-west-1.snowflakecomputing.com");

				props_tDBOutput_1.connection.setValue("authenticationType",
						org.talend.components.snowflake.tsnowflakeconnection.AuthenticationType.BASIC);

				props_tDBOutput_1.connection.setValue("warehouse", "TALEND_TEST");

				props_tDBOutput_1.connection.setValue("db", "TALEND_SB");

				props_tDBOutput_1.connection.setValue("schemaName", "SALES");

				props_tDBOutput_1.connection.setValue("role", "");

				props_tDBOutput_1.connection.setValue("jdbcParameters", "");

				props_tDBOutput_1.connection.userPassword.setValue("useAuth", false);

				props_tDBOutput_1.connection.userPassword.setValue("userId", "STEVEB");

				props_tDBOutput_1.connection.userPassword.setValue("password",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:/0Jv3CgW0groemHsmqe9cbmFEjWYwGiIVIVmyoJ3wud8K/myP38FzfKfnNc="));

				props_tDBOutput_1.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSnowflakeConnection");

				props_tDBOutput_1.table.setValue("tableName", "Agg_Crime_Values");

				props_tDBOutput_1.table.connection.setValue("loginTimeout", 15);

				props_tDBOutput_1.table.connection.setValue("account", "lt28176");

				props_tDBOutput_1.table.connection.setValue("regionID", "");

				props_tDBOutput_1.table.connection.setValue("useCustomRegion", false);

				props_tDBOutput_1.table.connection.setValue("jdbcUrlSuffix", ".eu-west-1.snowflakecomputing.com");

				props_tDBOutput_1.table.connection.setValue("authenticationType",
						org.talend.components.snowflake.tsnowflakeconnection.AuthenticationType.BASIC);

				props_tDBOutput_1.table.connection.setValue("warehouse", "TALEND_TEST");

				props_tDBOutput_1.table.connection.setValue("db", "TALEND_SB");

				props_tDBOutput_1.table.connection.setValue("schemaName", "SALES");

				props_tDBOutput_1.table.connection.setValue("role", "");

				props_tDBOutput_1.table.connection.setValue("jdbcParameters", "");

				props_tDBOutput_1.table.connection.userPassword.setValue("useAuth", false);

				props_tDBOutput_1.table.connection.userPassword.setValue("userId", "STEVEB");

				props_tDBOutput_1.table.connection.userPassword.setValue("password",
						routines.system.PasswordEncryptUtil.decryptPassword(
								"enc:routine.encryption.key.v1:0JxjlxOsoH+8tL82iENos94YCMd9+zbE45p2d1b9tg5mCurmVtsy8oaWi0w="));

				props_tDBOutput_1.table.connection.referencedComponent.setValue("referenceDefinitionName",
						"tSnowflakeConnection");

				class SchemaSettingTool_tDBOutput_1_2_fisrt {

					String getSchemaValue() {

						StringBuilder s = new StringBuilder();

						a("{\"type\":\"record\",", s);

						a("\"name\":\"SalesDataTest\",\"fields\":[{", s);

						a("\"name\":\"ORDERNUMBER\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"ORDERNUMBER\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"5\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"ORDERNUMBER\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"QUANTITYORDERED\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"QUANTITYORDERED\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"2\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"QUANTITYORDERED\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"PRICEEACH\",\"type\":[\"float\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"PRICEEACH\",\"di.column.talendType\":\"id_Float\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"5\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"PRICEEACH\",\"talend.field.precision\":\"3\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ORDERLINENUMBER\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"ORDERLINENUMBER\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"2\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"ORDERLINENUMBER\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"SALES\",\"type\":[\"float\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"SALES\",\"di.column.talendType\":\"id_Float\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"7\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"SALES\",\"talend.field.precision\":\"3\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ORDERDATE\",\"type\":[{\"type\":\"long\",\"java-class\":\"java.util.Date\"},\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"ORDERDATE\",\"di.column.talendType\":\"id_Date\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"d/M/yyyy h:mm\",\"talend.field.length\":\"40\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"ORDERDATE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"STATUS\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"STATUS\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"STATUS\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"QTR_ID\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"QTR_ID\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"1\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"QTR_ID\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"MONTH_ID\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"MONTH_ID\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"2\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"MONTH_ID\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"YEAR_ID\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"YEAR_ID\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"4\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"YEAR_ID\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"PRODUCTLINE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"PRODUCTLINE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"30\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"PRODUCTLINE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"MSRP\",\"type\":[\"int\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"MSRP\",\"di.column.talendType\":\"id_Integer\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"3\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"MSRP\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"PRODUCTCODE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"PRODUCTCODE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"PRODUCTCODE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CUSTOMERNAME\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"CUSTOMERNAME\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"50\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"CUSTOMERNAME\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"PHONE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"PHONE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"PHONE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ADDRESSLINE1\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"ADDRESSLINE1\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"50\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"ADDRESSLINE1\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"ADDRESSLINE2\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"ADDRESSLINE2\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"ADDRESSLINE2\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CITY\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"CITY\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"30\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"CITY\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"STATE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"STATE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"STATE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"POSTALCODE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"POSTALCODE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"POSTALCODE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"COUNTRY\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"COUNTRY\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"COUNTRY\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"TERRITORY\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"TERRITORY\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"TERRITORY\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CONTACTLASTNAME\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"CONTACTLASTNAME\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"CONTACTLASTNAME\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"CONTACTFIRSTNAME\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"CONTACTFIRSTNAME\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"CONTACTFIRSTNAME\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"},{",
								s);

						a("\"name\":\"DEALSIZE\",\"type\":[\"string\",\"null\"],\"di.table.comment\":\"\",\"talend.field.dbColumnName\":\"DEALSIZE\",\"di.column.talendType\":\"id_String\",\"di.column.isNullable\":\"true\",\"talend.field.pattern\":\"dd-MM-yyyy\",\"talend.field.length\":\"20\",\"di.column.relationshipType\":\"\",\"di.column.originalLength\":\"0\",\"di.table.label\":\"DEALSIZE\",\"talend.field.precision\":\"0\",\"di.column.relatedEntity\":\"\"}],\"di.table.name\":\"tDBOutput_1\",\"di.table.label\":\"SalesDataTest\"}",
								s);

						return s.toString();

					}

					void a(String part, StringBuilder strB) {
						strB.append(part);
					}

				}

				SchemaSettingTool_tDBOutput_1_2_fisrt sst_tDBOutput_1_2_fisrt = new SchemaSettingTool_tDBOutput_1_2_fisrt();

				props_tDBOutput_1.table.main.setValue("schema", new org.apache.avro.Schema.Parser()
						.setValidateDefaults(false).parse(sst_tDBOutput_1_2_fisrt.getSchemaValue()));

				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tDBOutput_1.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tDBOutput_1 = props_tDBOutput_1.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tDBOutput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tDBOutput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tDBOutput_1 + "_COMPONENT_RUNTIME_PROPERTIES");
						props_tDBOutput_1.connection.referencedComponent
								.setReference(referencedComponentProperties_tDBOutput_1);
					}
				}
				if (org.talend.components.api.properties.ComponentReferenceProperties.ReferenceType.COMPONENT_INSTANCE == props_tDBOutput_1.table.connection.referencedComponent.referenceType
						.getValue()) {
					final String referencedComponentInstanceId_tDBOutput_1 = props_tDBOutput_1.table.connection.referencedComponent.componentInstanceId
							.getStringValue();
					if (referencedComponentInstanceId_tDBOutput_1 != null) {
						org.talend.daikon.properties.Properties referencedComponentProperties_tDBOutput_1 = (org.talend.daikon.properties.Properties) globalMap
								.get(referencedComponentInstanceId_tDBOutput_1 + "_COMPONENT_RUNTIME_PROPERTIES");
						props_tDBOutput_1.table.connection.referencedComponent
								.setReference(referencedComponentProperties_tDBOutput_1);
					}
				}
				globalMap.put("tDBOutput_1_COMPONENT_RUNTIME_PROPERTIES", props_tDBOutput_1);
				globalMap.putIfAbsent("TALEND_PRODUCT_VERSION", "8.0");
				globalMap.put("TALEND_COMPONENTS_VERSION", "0.37.38");
				java.net.URL mappings_url_tDBOutput_1 = this.getClass().getResource("/xmlMappings");
				globalMap.put("tDBOutput_1_MAPPINGS_URL", mappings_url_tDBOutput_1);

				org.talend.components.api.container.RuntimeContainer container_tDBOutput_1 = new org.talend.components.api.container.RuntimeContainer() {
					public Object getComponentData(String componentId, String key) {
						return globalMap.get(componentId + "_" + key);
					}

					public void setComponentData(String componentId, String key, Object data) {
						globalMap.put(componentId + "_" + key, data);
					}

					public String getCurrentComponentId() {
						return "tDBOutput_1";
					}

					public Object getGlobalData(String key) {
						return globalMap.get(key);
					}
				};

				int nb_line_tDBOutput_1 = 0;

				org.talend.components.api.component.ConnectorTopology topology_tDBOutput_1 = null;
				topology_tDBOutput_1 = org.talend.components.api.component.ConnectorTopology.INCOMING;

				org.talend.daikon.runtime.RuntimeInfo runtime_info_tDBOutput_1 = def_tDBOutput_1.getRuntimeInfo(
						org.talend.components.api.component.runtime.ExecutionEngine.DI, props_tDBOutput_1,
						topology_tDBOutput_1);
				java.util.Set<org.talend.components.api.component.ConnectorTopology> supported_connector_topologies_tDBOutput_1 = def_tDBOutput_1
						.getSupportedConnectorTopologies();

				org.talend.components.api.component.runtime.RuntimableRuntime componentRuntime_tDBOutput_1 = (org.talend.components.api.component.runtime.RuntimableRuntime) (Class
						.forName(runtime_info_tDBOutput_1.getRuntimeClassName()).newInstance());
				org.talend.daikon.properties.ValidationResult initVr_tDBOutput_1 = componentRuntime_tDBOutput_1
						.initialize(container_tDBOutput_1, props_tDBOutput_1);

				if (initVr_tDBOutput_1.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
					throw new RuntimeException(initVr_tDBOutput_1.getMessage());
				}

				if (componentRuntime_tDBOutput_1 instanceof org.talend.components.api.component.runtime.ComponentDriverInitialization) {
					org.talend.components.api.component.runtime.ComponentDriverInitialization compDriverInitialization_tDBOutput_1 = (org.talend.components.api.component.runtime.ComponentDriverInitialization) componentRuntime_tDBOutput_1;
					compDriverInitialization_tDBOutput_1.runAtDriver(container_tDBOutput_1);
				}

				org.talend.components.api.component.runtime.SourceOrSink sourceOrSink_tDBOutput_1 = null;
				if (componentRuntime_tDBOutput_1 instanceof org.talend.components.api.component.runtime.SourceOrSink) {
					sourceOrSink_tDBOutput_1 = (org.talend.components.api.component.runtime.SourceOrSink) componentRuntime_tDBOutput_1;
					if (doesNodeBelongToRequest_tDBOutput_1) {
						org.talend.daikon.properties.ValidationResult vr_tDBOutput_1 = sourceOrSink_tDBOutput_1
								.validate(container_tDBOutput_1);
						if (vr_tDBOutput_1.getStatus() == org.talend.daikon.properties.ValidationResult.Result.ERROR) {
							throw new RuntimeException(vr_tDBOutput_1.getMessage());
						}
					}
				}

				org.talend.codegen.enforcer.IncomingSchemaEnforcer incomingEnforcer_tDBOutput_1 = null;
				if (sourceOrSink_tDBOutput_1 instanceof org.talend.components.api.component.runtime.Sink) {
					org.talend.components.api.component.runtime.Sink sink_tDBOutput_1 = (org.talend.components.api.component.runtime.Sink) sourceOrSink_tDBOutput_1;
					org.talend.components.api.component.runtime.WriteOperation writeOperation_tDBOutput_1 = sink_tDBOutput_1
							.createWriteOperation();
					if (doesNodeBelongToRequest_tDBOutput_1) {
						writeOperation_tDBOutput_1.initialize(container_tDBOutput_1);
					}
					writer_tDBOutput_1 = writeOperation_tDBOutput_1.createWriter(container_tDBOutput_1);
					if (doesNodeBelongToRequest_tDBOutput_1) {
						writer_tDBOutput_1.open("tDBOutput_1");
					}

					resourceMap.put("writer_tDBOutput_1", writer_tDBOutput_1);
				} // end of "sourceOrSink_tDBOutput_1 instanceof ...Sink"
				org.talend.components.api.component.Connector c_tDBOutput_1 = null;
				for (org.talend.components.api.component.Connector currentConnector : props_tDBOutput_1
						.getAvailableConnectors(null, false)) {
					if (currentConnector.getName().equals("MAIN")) {
						c_tDBOutput_1 = currentConnector;
						break;
					}
				}
				org.apache.avro.Schema designSchema_tDBOutput_1 = props_tDBOutput_1.getSchema(c_tDBOutput_1, false);
				incomingEnforcer_tDBOutput_1 = new org.talend.codegen.enforcer.IncomingSchemaEnforcer(
						designSchema_tDBOutput_1);

				java.lang.Iterable<?> outgoingMainRecordsList_tDBOutput_1 = new java.util.ArrayList<Object>();
				java.util.Iterator outgoingMainRecordsIt_tDBOutput_1 = null;

				/**
				 * [tDBOutput_1 begin ] stop
				 */

				/**
				 * [tFileInputDelimited_1 begin ] start
				 */

				sh("tFileInputDelimited_1");

				s(currentComponent = "tFileInputDelimited_1");

				cLabel = "sales_data";

				int tos_count_tFileInputDelimited_1 = 0;

				if (log.isDebugEnabled())
					log.debug("tFileInputDelimited_1 - " + ("Start to work."));
				if (log.isDebugEnabled()) {
					class BytesLimit65535_tFileInputDelimited_1 {
						public void limitLog4jByte() throws Exception {
							StringBuilder log4jParamters_tFileInputDelimited_1 = new StringBuilder();
							log4jParamters_tFileInputDelimited_1.append("Parameters:");
							log4jParamters_tFileInputDelimited_1.append("USE_EXISTING_DYNAMIC" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("FILENAME" + " = "
									+ "\"C:/Users/SteveBates/OneDrive - CATALYST IT SOLUTIONS LTD/Documents/SALES_DATA_1.csv\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("CSV_OPTION" + " = " + "true");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("CSVROWSEPARATOR" + " = " + "\"\\n\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("FIELDSEPARATOR" + " = " + "\",\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("ESCAPE_CHAR" + " = " + "\"'\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("TEXT_ENCLOSURE" + " = " + "\"\"\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("HEADER" + " = " + "1");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("FOOTER" + " = " + "0");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("LIMIT" + " = " + "");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("REMOVE_EMPTY_ROW" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("UNCOMPRESS" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("DIE_ON_ERROR" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("ADVANCED_SEPARATOR" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("TRIMALL" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("TRIMSELECT" + " = " + "[{TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("ORDERNUMBER") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("QUANTITYORDERED") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("PRICEEACH") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
									+ ("ORDERLINENUMBER") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("SALES")
									+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("ORDERDATE") + "}, {TRIM="
									+ ("false") + ", SCHEMA_COLUMN=" + ("STATUS") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("QTR_ID") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
									+ ("MONTH_ID") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("YEAR_ID")
									+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("PRODUCTLINE") + "}, {TRIM="
									+ ("false") + ", SCHEMA_COLUMN=" + ("MSRP") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("PRODUCTCODE") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("CUSTOMERNAME") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("PHONE") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
									+ ("ADDRESSLINE1") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("ADDRESSLINE2")
									+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("CITY") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("STATE") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN="
									+ ("POSTALCODE") + "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("COUNTRY")
									+ "}, {TRIM=" + ("false") + ", SCHEMA_COLUMN=" + ("TERRITORY") + "}, {TRIM="
									+ ("false") + ", SCHEMA_COLUMN=" + ("CONTACTLASTNAME") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("CONTACTFIRSTNAME") + "}, {TRIM=" + ("false")
									+ ", SCHEMA_COLUMN=" + ("DEALSIZE") + "}]");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("CHECK_FIELDS_NUM" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("CHECK_DATE" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("ENCODING" + " = " + "\"UTF-8\"");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("ENABLE_DECODE" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							log4jParamters_tFileInputDelimited_1.append("USE_HEADER_AS_IS" + " = " + "false");
							log4jParamters_tFileInputDelimited_1.append(" | ");
							if (log.isDebugEnabled())
								log.debug("tFileInputDelimited_1 - " + (log4jParamters_tFileInputDelimited_1));
						}
					}
					new BytesLimit65535_tFileInputDelimited_1().limitLog4jByte();
				}
				if (enableLogStash) {
					talendJobLog.addCM("tFileInputDelimited_1", "sales_data", "tFileInputDelimited");
					talendJobLogProcess(globalMap);
					s(currentComponent);
				}

				final routines.system.RowState rowstate_tFileInputDelimited_1 = new routines.system.RowState();

				int nb_line_tFileInputDelimited_1 = 0;
				int footer_tFileInputDelimited_1 = 0;
				int totalLinetFileInputDelimited_1 = 0;
				int limittFileInputDelimited_1 = -1;
				int lastLinetFileInputDelimited_1 = -1;

				char fieldSeparator_tFileInputDelimited_1[] = null;

				// support passing value (property: Field Separator) by 'context.fs' or
				// 'globalMap.get("fs")'.
				if (((String) ",").length() > 0) {
					fieldSeparator_tFileInputDelimited_1 = ((String) ",").toCharArray();
				} else {
					throw new IllegalArgumentException("Field Separator must be assigned a char.");
				}

				char rowSeparator_tFileInputDelimited_1[] = null;

				// support passing value (property: Row Separator) by 'context.rs' or
				// 'globalMap.get("rs")'.
				if (((String) "\n").length() > 0) {
					rowSeparator_tFileInputDelimited_1 = ((String) "\n").toCharArray();
				} else {
					throw new IllegalArgumentException("Row Separator must be assigned a char.");
				}

				Object filename_tFileInputDelimited_1 = /** Start field tFileInputDelimited_1:FILENAME */
						"C:/Users/SteveBates/OneDrive - CATALYST IT SOLUTIONS LTD/Documents/SALES_DATA_1.csv"/**
																												 * End
																												 * field
																												 * tFileInputDelimited_1:FILENAME
																												 */
				;
				com.talend.csv.CSVReader csvReadertFileInputDelimited_1 = null;

				try {

					String[] rowtFileInputDelimited_1 = null;
					int currentLinetFileInputDelimited_1 = 0;
					int outputLinetFileInputDelimited_1 = 0;
					try {// TD110 begin
						if (filename_tFileInputDelimited_1 instanceof java.io.InputStream) {

							int footer_value_tFileInputDelimited_1 = 0;
							if (footer_value_tFileInputDelimited_1 > 0) {
								throw new java.lang.Exception(
										"When the input source is a stream,footer shouldn't be bigger than 0.");
							}

							csvReadertFileInputDelimited_1 = new com.talend.csv.CSVReader(
									(java.io.InputStream) filename_tFileInputDelimited_1,
									fieldSeparator_tFileInputDelimited_1[0], "UTF-8");
						} else {
							csvReadertFileInputDelimited_1 = new com.talend.csv.CSVReader(
									String.valueOf(filename_tFileInputDelimited_1),
									fieldSeparator_tFileInputDelimited_1[0], "UTF-8");
						}

						csvReadertFileInputDelimited_1.setTrimWhitespace(false);
						if ((rowSeparator_tFileInputDelimited_1[0] != '\n')
								&& (rowSeparator_tFileInputDelimited_1[0] != '\r'))
							csvReadertFileInputDelimited_1.setLineEnd("" + rowSeparator_tFileInputDelimited_1[0]);

						csvReadertFileInputDelimited_1.setQuoteChar('"');

						// ?????doesn't work for other escapeChar
						// the default escape mode is double escape
						csvReadertFileInputDelimited_1.setEscapeChar(csvReadertFileInputDelimited_1.getQuoteChar());

						if (footer_tFileInputDelimited_1 > 0) {
							for (totalLinetFileInputDelimited_1 = 0; totalLinetFileInputDelimited_1 < 1; totalLinetFileInputDelimited_1++) {
								csvReadertFileInputDelimited_1.readNext();
							}
							csvReadertFileInputDelimited_1.setSkipEmptyRecords(false);
							while (csvReadertFileInputDelimited_1.readNext()) {

								totalLinetFileInputDelimited_1++;

							}
							int lastLineTemptFileInputDelimited_1 = totalLinetFileInputDelimited_1
									- footer_tFileInputDelimited_1 < 0 ? 0
											: totalLinetFileInputDelimited_1 - footer_tFileInputDelimited_1;
							if (lastLinetFileInputDelimited_1 > 0) {
								lastLinetFileInputDelimited_1 = lastLinetFileInputDelimited_1 < lastLineTemptFileInputDelimited_1
										? lastLinetFileInputDelimited_1
										: lastLineTemptFileInputDelimited_1;
							} else {
								lastLinetFileInputDelimited_1 = lastLineTemptFileInputDelimited_1;
							}

							csvReadertFileInputDelimited_1.close();
							if (filename_tFileInputDelimited_1 instanceof java.io.InputStream) {
								csvReadertFileInputDelimited_1 = new com.talend.csv.CSVReader(
										(java.io.InputStream) filename_tFileInputDelimited_1,
										fieldSeparator_tFileInputDelimited_1[0], "UTF-8");
							} else {
								csvReadertFileInputDelimited_1 = new com.talend.csv.CSVReader(
										String.valueOf(filename_tFileInputDelimited_1),
										fieldSeparator_tFileInputDelimited_1[0], "UTF-8");
							}
							csvReadertFileInputDelimited_1.setTrimWhitespace(false);
							if ((rowSeparator_tFileInputDelimited_1[0] != '\n')
									&& (rowSeparator_tFileInputDelimited_1[0] != '\r'))
								csvReadertFileInputDelimited_1.setLineEnd("" + rowSeparator_tFileInputDelimited_1[0]);

							csvReadertFileInputDelimited_1.setQuoteChar('"');

							// ?????doesn't work for other escapeChar
							// the default escape mode is double escape
							csvReadertFileInputDelimited_1.setEscapeChar(csvReadertFileInputDelimited_1.getQuoteChar());

						}

						if (limittFileInputDelimited_1 != 0) {
							for (currentLinetFileInputDelimited_1 = 0; currentLinetFileInputDelimited_1 < 1; currentLinetFileInputDelimited_1++) {
								csvReadertFileInputDelimited_1.readNext();
							}
						}
						csvReadertFileInputDelimited_1.setSkipEmptyRecords(false);

					} catch (java.lang.Exception e) {
						globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE", e.getMessage());

						log.error("tFileInputDelimited_1 - " + e.getMessage());

						System.err.println(e.getMessage());

					} // TD110 end

					log.info("tFileInputDelimited_1 - Retrieving records from the datasource.");

					while (limittFileInputDelimited_1 != 0 && csvReadertFileInputDelimited_1 != null
							&& csvReadertFileInputDelimited_1.readNext()) {
						rowstate_tFileInputDelimited_1.reset();

						rowtFileInputDelimited_1 = csvReadertFileInputDelimited_1.getValues();

						currentLinetFileInputDelimited_1++;

						if (lastLinetFileInputDelimited_1 > -1
								&& currentLinetFileInputDelimited_1 > lastLinetFileInputDelimited_1) {
							break;
						}
						outputLinetFileInputDelimited_1++;
						if (limittFileInputDelimited_1 > 0
								&& outputLinetFileInputDelimited_1 > limittFileInputDelimited_1) {
							break;
						}

						row1 = null;

						boolean whetherReject_tFileInputDelimited_1 = false;
						row1 = new row1Struct();
						try {

							char fieldSeparator_tFileInputDelimited_1_ListType[] = null;
							// support passing value (property: Field Separator) by 'context.fs' or
							// 'globalMap.get("fs")'.
							if (((String) ",").length() > 0) {
								fieldSeparator_tFileInputDelimited_1_ListType = ((String) ",").toCharArray();
							} else {
								throw new IllegalArgumentException("Field Separator must be assigned a char.");
							}
							if (rowtFileInputDelimited_1.length == 1 && ("\015").equals(rowtFileInputDelimited_1[0])) {// empty
																														// line
																														// when
																														// row
																														// separator
																														// is
																														// '\n'

								row1.ORDERNUMBER = null;

								row1.QUANTITYORDERED = null;

								row1.PRICEEACH = null;

								row1.ORDERLINENUMBER = null;

								row1.SALES = null;

								row1.ORDERDATE = null;

								row1.STATUS = null;

								row1.QTR_ID = null;

								row1.MONTH_ID = null;

								row1.YEAR_ID = null;

								row1.PRODUCTLINE = null;

								row1.MSRP = null;

								row1.PRODUCTCODE = null;

								row1.CUSTOMERNAME = null;

								row1.PHONE = null;

								row1.ADDRESSLINE1 = null;

								row1.ADDRESSLINE2 = null;

								row1.CITY = null;

								row1.STATE = null;

								row1.POSTALCODE = null;

								row1.COUNTRY = null;

								row1.TERRITORY = null;

								row1.CONTACTLASTNAME = null;

								row1.CONTACTFIRSTNAME = null;

								row1.DEALSIZE = null;

							} else {

								int columnIndexWithD_tFileInputDelimited_1 = 0; // Column Index

								columnIndexWithD_tFileInputDelimited_1 = 0;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.ORDERNUMBER = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"ORDERNUMBER", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.ORDERNUMBER = null;

									}

								} else {

									row1.ORDERNUMBER = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 1;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.QUANTITYORDERED = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"QUANTITYORDERED", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.QUANTITYORDERED = null;

									}

								} else {

									row1.QUANTITYORDERED = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 2;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.PRICEEACH = ParserUtils.parseTo_Float(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"PRICEEACH", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.PRICEEACH = null;

									}

								} else {

									row1.PRICEEACH = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 3;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.ORDERLINENUMBER = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"ORDERLINENUMBER", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.ORDERLINENUMBER = null;

									}

								} else {

									row1.ORDERLINENUMBER = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 4;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.SALES = ParserUtils.parseTo_Float(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"SALES", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.SALES = null;

									}

								} else {

									row1.SALES = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 5;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.ORDERDATE = ParserUtils.parseTo_Date(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
													"d/M/yyyy h:mm");

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"ORDERDATE", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.ORDERDATE = null;

									}

								} else {

									row1.ORDERDATE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 6;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.STATUS = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.STATUS = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 7;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.QTR_ID = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"QTR_ID", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.QTR_ID = null;

									}

								} else {

									row1.QTR_ID = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 8;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.MONTH_ID = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"MONTH_ID", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.MONTH_ID = null;

									}

								} else {

									row1.MONTH_ID = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 9;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.YEAR_ID = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"YEAR_ID", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.YEAR_ID = null;

									}

								} else {

									row1.YEAR_ID = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 10;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.PRODUCTLINE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.PRODUCTLINE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 11;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									if (rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1].length() > 0) {
										try {

											row1.MSRP = ParserUtils.parseTo_Integer(
													rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1]);

										} catch (java.lang.Exception ex_tFileInputDelimited_1) {
											globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE",
													ex_tFileInputDelimited_1.getMessage());
											rowstate_tFileInputDelimited_1.setException(new RuntimeException(String
													.format("Couldn't parse value for column '%s' in '%s', value is '%s'. Details: %s",
															"MSRP", "row1",
															rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1],
															ex_tFileInputDelimited_1),
													ex_tFileInputDelimited_1));
										}
									} else {

										row1.MSRP = null;

									}

								} else {

									row1.MSRP = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 12;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.PRODUCTCODE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.PRODUCTCODE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 13;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.CUSTOMERNAME = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.CUSTOMERNAME = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 14;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.PHONE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.PHONE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 15;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.ADDRESSLINE1 = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.ADDRESSLINE1 = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 16;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.ADDRESSLINE2 = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.ADDRESSLINE2 = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 17;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.CITY = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.CITY = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 18;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.STATE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.STATE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 19;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.POSTALCODE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.POSTALCODE = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 20;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.COUNTRY = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.COUNTRY = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 21;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.TERRITORY = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.TERRITORY = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 22;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.CONTACTLASTNAME = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.CONTACTLASTNAME = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 23;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.CONTACTFIRSTNAME = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.CONTACTFIRSTNAME = null;

								}

								columnIndexWithD_tFileInputDelimited_1 = 24;

								if (columnIndexWithD_tFileInputDelimited_1 < rowtFileInputDelimited_1.length) {

									row1.DEALSIZE = rowtFileInputDelimited_1[columnIndexWithD_tFileInputDelimited_1];

								} else {

									row1.DEALSIZE = null;

								}

							}

							if (rowstate_tFileInputDelimited_1.getException() != null) {
								throw rowstate_tFileInputDelimited_1.getException();
							}

						} catch (java.lang.Exception e) {
							globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE", e.getMessage());
							whetherReject_tFileInputDelimited_1 = true;

							log.error("tFileInputDelimited_1 - " + e.getMessage());

							System.err.println(e.getMessage());
							row1 = null;

							globalMap.put("tFileInputDelimited_1_ERROR_MESSAGE", e.getMessage());

						}

						log.debug("tFileInputDelimited_1 - Retrieving the record " + (nb_line_tFileInputDelimited_1 + 1)
								+ ".");

						/**
						 * [tFileInputDelimited_1 begin ] stop
						 */

						/**
						 * [tFileInputDelimited_1 main ] start
						 */

						s(currentComponent = "tFileInputDelimited_1");

						cLabel = "sales_data";

						tos_count_tFileInputDelimited_1++;

						/**
						 * [tFileInputDelimited_1 main ] stop
						 */

						/**
						 * [tFileInputDelimited_1 process_data_begin ] start
						 */

						s(currentComponent = "tFileInputDelimited_1");

						cLabel = "sales_data";

						/**
						 * [tFileInputDelimited_1 process_data_begin ] stop
						 */

// Start of branch "row1"
						if (row1 != null) {

							/**
							 * [tDBOutput_1 main ] start
							 */

							s(currentComponent = "tDBOutput_1");

							cLabel = "sales_sf";

							if (runStat.update(execStat, enableLogStash, iterateId, 1, 1

									, "row1", "tFileInputDelimited_1", "sales_data", "tFileInputDelimited",
									"tDBOutput_1", "sales_sf", "tSnowflakeOutput"

							)) {
								talendJobLogProcess(globalMap);
							}

							if (log.isTraceEnabled()) {
								log.trace("row1 - " + (row1 == null ? "" : row1.toLogString()));
							}

							if (incomingEnforcer_tDBOutput_1 != null) {
								incomingEnforcer_tDBOutput_1.createNewRecord();
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("ORDERNUMBER") != null) {
								incomingEnforcer_tDBOutput_1.put("ORDERNUMBER", row1.ORDERNUMBER);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("QUANTITYORDERED") != null) {
								incomingEnforcer_tDBOutput_1.put("QUANTITYORDERED", row1.QUANTITYORDERED);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("PRICEEACH") != null) {
								incomingEnforcer_tDBOutput_1.put("PRICEEACH", row1.PRICEEACH);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("ORDERLINENUMBER") != null) {
								incomingEnforcer_tDBOutput_1.put("ORDERLINENUMBER", row1.ORDERLINENUMBER);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("SALES") != null) {
								incomingEnforcer_tDBOutput_1.put("SALES", row1.SALES);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("ORDERDATE") != null) {
								incomingEnforcer_tDBOutput_1.put("ORDERDATE", row1.ORDERDATE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("STATUS") != null) {
								incomingEnforcer_tDBOutput_1.put("STATUS", row1.STATUS);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("QTR_ID") != null) {
								incomingEnforcer_tDBOutput_1.put("QTR_ID", row1.QTR_ID);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("MONTH_ID") != null) {
								incomingEnforcer_tDBOutput_1.put("MONTH_ID", row1.MONTH_ID);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("YEAR_ID") != null) {
								incomingEnforcer_tDBOutput_1.put("YEAR_ID", row1.YEAR_ID);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("PRODUCTLINE") != null) {
								incomingEnforcer_tDBOutput_1.put("PRODUCTLINE", row1.PRODUCTLINE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("MSRP") != null) {
								incomingEnforcer_tDBOutput_1.put("MSRP", row1.MSRP);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("PRODUCTCODE") != null) {
								incomingEnforcer_tDBOutput_1.put("PRODUCTCODE", row1.PRODUCTCODE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("CUSTOMERNAME") != null) {
								incomingEnforcer_tDBOutput_1.put("CUSTOMERNAME", row1.CUSTOMERNAME);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("PHONE") != null) {
								incomingEnforcer_tDBOutput_1.put("PHONE", row1.PHONE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("ADDRESSLINE1") != null) {
								incomingEnforcer_tDBOutput_1.put("ADDRESSLINE1", row1.ADDRESSLINE1);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("ADDRESSLINE2") != null) {
								incomingEnforcer_tDBOutput_1.put("ADDRESSLINE2", row1.ADDRESSLINE2);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("CITY") != null) {
								incomingEnforcer_tDBOutput_1.put("CITY", row1.CITY);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("STATE") != null) {
								incomingEnforcer_tDBOutput_1.put("STATE", row1.STATE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("POSTALCODE") != null) {
								incomingEnforcer_tDBOutput_1.put("POSTALCODE", row1.POSTALCODE);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("COUNTRY") != null) {
								incomingEnforcer_tDBOutput_1.put("COUNTRY", row1.COUNTRY);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("TERRITORY") != null) {
								incomingEnforcer_tDBOutput_1.put("TERRITORY", row1.TERRITORY);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("CONTACTLASTNAME") != null) {
								incomingEnforcer_tDBOutput_1.put("CONTACTLASTNAME", row1.CONTACTLASTNAME);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null && incomingEnforcer_tDBOutput_1.getRuntimeSchema()
									.getField("CONTACTFIRSTNAME") != null) {
								incomingEnforcer_tDBOutput_1.put("CONTACTFIRSTNAME", row1.CONTACTFIRSTNAME);
							}
							// skip the put action if the input column doesn't appear in component runtime
							// schema
							if (incomingEnforcer_tDBOutput_1 != null
									&& incomingEnforcer_tDBOutput_1.getRuntimeSchema().getField("DEALSIZE") != null) {
								incomingEnforcer_tDBOutput_1.put("DEALSIZE", row1.DEALSIZE);
							}

							org.apache.avro.generic.IndexedRecord data_tDBOutput_1 = null;
							if (incomingEnforcer_tDBOutput_1 != null) {
								data_tDBOutput_1 = incomingEnforcer_tDBOutput_1.getCurrentRecord();
							}

							if (writer_tDBOutput_1 != null && data_tDBOutput_1 != null) {
								writer_tDBOutput_1.write(data_tDBOutput_1);
							}

							nb_line_tDBOutput_1++;

							tos_count_tDBOutput_1++;

							/**
							 * [tDBOutput_1 main ] stop
							 */

							/**
							 * [tDBOutput_1 process_data_begin ] start
							 */

							s(currentComponent = "tDBOutput_1");

							cLabel = "sales_sf";

							/**
							 * [tDBOutput_1 process_data_begin ] stop
							 */

							/**
							 * [tDBOutput_1 process_data_end ] start
							 */

							s(currentComponent = "tDBOutput_1");

							cLabel = "sales_sf";

							/**
							 * [tDBOutput_1 process_data_end ] stop
							 */

						} // End of branch "row1"

						/**
						 * [tFileInputDelimited_1 process_data_end ] start
						 */

						s(currentComponent = "tFileInputDelimited_1");

						cLabel = "sales_data";

						/**
						 * [tFileInputDelimited_1 process_data_end ] stop
						 */

						/**
						 * [tFileInputDelimited_1 end ] start
						 */

						s(currentComponent = "tFileInputDelimited_1");

						cLabel = "sales_data";

						nb_line_tFileInputDelimited_1++;
					}

				} finally {
					if (!(filename_tFileInputDelimited_1 instanceof java.io.InputStream)) {
						if (csvReadertFileInputDelimited_1 != null) {
							csvReadertFileInputDelimited_1.close();
						}
					}
					if (csvReadertFileInputDelimited_1 != null) {
						globalMap.put("tFileInputDelimited_1_NB_LINE", nb_line_tFileInputDelimited_1);
					}

					log.info("tFileInputDelimited_1 - Retrieved records count: " + nb_line_tFileInputDelimited_1 + ".");

				}

				if (log.isDebugEnabled())
					log.debug("tFileInputDelimited_1 - " + ("Done."));

				ok_Hash.put("tFileInputDelimited_1", true);
				end_Hash.put("tFileInputDelimited_1", System.currentTimeMillis());

				/**
				 * [tFileInputDelimited_1 end ] stop
				 */

				/**
				 * [tDBOutput_1 end ] start
				 */

				s(currentComponent = "tDBOutput_1");

				cLabel = "sales_sf";

// end of generic

				resourceMap.put("finish_tDBOutput_1", Boolean.TRUE);

				java.util.Map<String, Object> resultMap_tDBOutput_1 = null;
				if (writer_tDBOutput_1 != null) {
					org.talend.components.api.component.runtime.Result resultObject_tDBOutput_1 = (org.talend.components.api.component.runtime.Result) writer_tDBOutput_1
							.close();
					resultMap_tDBOutput_1 = writer_tDBOutput_1.getWriteOperation()
							.finalize(java.util.Arrays.<org.talend.components.api.component.runtime.Result>asList(
									resultObject_tDBOutput_1), container_tDBOutput_1);
				}
				if (resultMap_tDBOutput_1 != null) {
					for (java.util.Map.Entry<String, Object> entry_tDBOutput_1 : resultMap_tDBOutput_1.entrySet()) {
						switch (entry_tDBOutput_1.getKey()) {
						case org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE:
							container_tDBOutput_1.setComponentData("tDBOutput_1", "ERROR_MESSAGE",
									entry_tDBOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT:
							container_tDBOutput_1.setComponentData("tDBOutput_1", "NB_LINE",
									entry_tDBOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_SUCCESS_RECORD_COUNT:
							container_tDBOutput_1.setComponentData("tDBOutput_1", "NB_SUCCESS",
									entry_tDBOutput_1.getValue());
							break;
						case org.talend.components.api.component.ComponentDefinition.RETURN_REJECT_RECORD_COUNT:
							container_tDBOutput_1.setComponentData("tDBOutput_1", "NB_REJECT",
									entry_tDBOutput_1.getValue());
							break;
						default:
							StringBuilder studio_key_tDBOutput_1 = new StringBuilder();
							for (int i_tDBOutput_1 = 0; i_tDBOutput_1 < entry_tDBOutput_1.getKey()
									.length(); i_tDBOutput_1++) {
								char ch_tDBOutput_1 = entry_tDBOutput_1.getKey().charAt(i_tDBOutput_1);
								if (Character.isUpperCase(ch_tDBOutput_1) && i_tDBOutput_1 > 0) {
									studio_key_tDBOutput_1.append('_');
								}
								studio_key_tDBOutput_1.append(ch_tDBOutput_1);
							}
							container_tDBOutput_1.setComponentData("tDBOutput_1",
									studio_key_tDBOutput_1.toString().toUpperCase(java.util.Locale.ENGLISH),
									entry_tDBOutput_1.getValue());
							break;
						}
					}
				}

				if (runStat.updateStatAndLog(execStat, enableLogStash, resourceMap, iterateId, "row1", 2, 0,
						"tFileInputDelimited_1", "sales_data", "tFileInputDelimited", "tDBOutput_1", "sales_sf",
						"tSnowflakeOutput", "output")) {
					talendJobLogProcess(globalMap);
				}

				ok_Hash.put("tDBOutput_1", true);
				end_Hash.put("tDBOutput_1", System.currentTimeMillis());

				/**
				 * [tDBOutput_1 end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [tFileInputDelimited_1 finally ] start
				 */

				s(currentComponent = "tFileInputDelimited_1");

				cLabel = "sales_data";

				/**
				 * [tFileInputDelimited_1 finally ] stop
				 */

				/**
				 * [tDBOutput_1 finally ] start
				 */

				s(currentComponent = "tDBOutput_1");

				cLabel = "sales_sf";

// finally of generic

				if (resourceMap.get("finish_tDBOutput_1") == null) {
					if (resourceMap.get("writer_tDBOutput_1") != null) {
						try {
							((org.talend.components.api.component.runtime.Writer) resourceMap.get("writer_tDBOutput_1"))
									.close();
						} catch (java.io.IOException e_tDBOutput_1) {
							String errorMessage_tDBOutput_1 = "failed to release the resource in tDBOutput_1 :"
									+ e_tDBOutput_1.getMessage();
							System.err.println(errorMessage_tDBOutput_1);
						}
					}
				}

				/**
				 * [tDBOutput_1 finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", 1);
	}

	public void talendJobLogProcess(final java.util.Map<String, Object> globalMap) throws TalendException {
		globalMap.put("talendJobLog_SUBPROCESS_STATE", 0);

		final boolean execStat = this.execStat;

		String iterateId = "";

		String currentComponent = "";
		s("none");
		String cLabel = null;
		java.util.Map<String, Object> resourceMap = new java.util.HashMap<String, Object>();

		try {
			// TDI-39566 avoid throwing an useless Exception
			boolean resumeIt = true;
			if (globalResumeTicket == false && resumeEntryMethodName != null) {
				String currentMethodName = new java.lang.Exception().getStackTrace()[0].getMethodName();
				resumeIt = resumeEntryMethodName.equals(currentMethodName);
			}
			if (resumeIt || globalResumeTicket) { // start the resume
				globalResumeTicket = true;

				/**
				 * [talendJobLog begin ] start
				 */

				sh("talendJobLog");

				s(currentComponent = "talendJobLog");

				int tos_count_talendJobLog = 0;

				for (JobStructureCatcherUtils.JobStructureCatcherMessage jcm : talendJobLog.getMessages()) {
					org.talend.job.audit.JobContextBuilder builder_talendJobLog = org.talend.job.audit.JobContextBuilder
							.create().jobName(jcm.job_name).jobId(jcm.job_id).jobVersion(jcm.job_version)
							.custom("process_id", jcm.pid).custom("thread_id", jcm.tid).custom("pid", pid)
							.custom("father_pid", fatherPid).custom("root_pid", rootPid);
					org.talend.logging.audit.Context log_context_talendJobLog = null;

					if (jcm.log_type == JobStructureCatcherUtils.LogType.PERFORMANCE) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.sourceId(jcm.sourceId)
								.sourceLabel(jcm.sourceLabel).sourceConnectorType(jcm.sourceComponentName)
								.targetId(jcm.targetId).targetLabel(jcm.targetLabel)
								.targetConnectorType(jcm.targetComponentName).connectionName(jcm.current_connector)
								.rows(jcm.row_count).duration(duration).build();
						auditLogger_talendJobLog.flowExecution(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBSTART) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).build();
						auditLogger_talendJobLog.jobstart(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBEND) {
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment).duration(duration)
								.status(jcm.status).build();
						auditLogger_talendJobLog.jobstop(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.RUNCOMPONENT) {
						log_context_talendJobLog = builder_talendJobLog.timestamp(jcm.moment)
								.connectorType(jcm.component_name).connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label).build();
						auditLogger_talendJobLog.runcomponent(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWINPUT) {// log current component
																							// input line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowInput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.FLOWOUTPUT) {// log current component
																								// output/reject line
						long timeMS = jcm.end_time - jcm.start_time;
						String duration = String.valueOf(timeMS);

						log_context_talendJobLog = builder_talendJobLog.connectorType(jcm.component_name)
								.connectorId(jcm.component_id).connectorLabel(jcm.component_label)
								.connectionName(jcm.current_connector).connectionType(jcm.current_connector_type)
								.rows(jcm.total_row_number).duration(duration).build();
						auditLogger_talendJobLog.flowOutput(log_context_talendJobLog);
					} else if (jcm.log_type == JobStructureCatcherUtils.LogType.JOBERROR) {
						java.lang.Exception e_talendJobLog = jcm.exception;
						if (e_talendJobLog != null) {
							try (java.io.StringWriter sw_talendJobLog = new java.io.StringWriter();
									java.io.PrintWriter pw_talendJobLog = new java.io.PrintWriter(sw_talendJobLog)) {
								e_talendJobLog.printStackTrace(pw_talendJobLog);
								builder_talendJobLog.custom("stacktrace", sw_talendJobLog.getBuffer().substring(0,
										java.lang.Math.min(sw_talendJobLog.getBuffer().length(), 512)));
							}
						}

						if (jcm.extra_info != null) {
							builder_talendJobLog.connectorId(jcm.component_id).custom("extra_info", jcm.extra_info);
						}

						log_context_talendJobLog = builder_talendJobLog
								.connectorType(jcm.component_id.substring(0, jcm.component_id.lastIndexOf('_')))
								.connectorId(jcm.component_id)
								.connectorLabel(jcm.component_label == null ? jcm.component_id : jcm.component_label)
								.build();

						auditLogger_talendJobLog.exception(log_context_talendJobLog);
					}

				}

				/**
				 * [talendJobLog begin ] stop
				 */

				/**
				 * [talendJobLog main ] start
				 */

				s(currentComponent = "talendJobLog");

				tos_count_talendJobLog++;

				/**
				 * [talendJobLog main ] stop
				 */

				/**
				 * [talendJobLog process_data_begin ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_begin ] stop
				 */

				/**
				 * [talendJobLog process_data_end ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog process_data_end ] stop
				 */

				/**
				 * [talendJobLog end ] start
				 */

				s(currentComponent = "talendJobLog");

				ok_Hash.put("talendJobLog", true);
				end_Hash.put("talendJobLog", System.currentTimeMillis());

				/**
				 * [talendJobLog end ] stop
				 */

			} // end the resume

		} catch (java.lang.Exception e) {

			if (!(e instanceof TalendException)) {
				log.fatal(currentComponent + " " + e.getMessage(), e);
			}

			TalendException te = new TalendException(e, currentComponent, cLabel, globalMap);

			throw te;
		} catch (java.lang.Error error) {

			runStat.stopThreadStat();

			throw error;
		} finally {

			try {

				/**
				 * [talendJobLog finally ] start
				 */

				s(currentComponent = "talendJobLog");

				/**
				 * [talendJobLog finally ] stop
				 */

			} catch (java.lang.Exception e) {
				// ignore
			} catch (java.lang.Error error) {
				// ignore
			}
			resourceMap = null;
		}

		globalMap.put("talendJobLog_SUBPROCESS_STATE", 1);
	}

	public String resuming_logs_dir_path = null;
	public String resuming_checkpoint_path = null;
	public String parent_part_launcher = null;
	private String resumeEntryMethodName = null;
	private boolean globalResumeTicket = false;

	public boolean watch = false;
	// portStats is null, it means don't execute the statistics
	public Integer portStats = null;
	public int portTraces = 4334;
	public String clientHost;
	public String defaultClientHost = "localhost";
	public String contextStr = "Default";
	public boolean isDefaultContext = true;
	public String pid = "0";
	public String rootPid = null;
	public String fatherPid = null;
	public String fatherNode = null;
	public long startTime = 0;
	public boolean isChildJob = false;
	public String log4jLevel = "";

	private boolean enableLogStash;

	private boolean execStat = true;

	private ThreadLocal<java.util.Map<String, String>> threadLocal = new ThreadLocal<java.util.Map<String, String>>() {
		protected java.util.Map<String, String> initialValue() {
			java.util.Map<String, String> threadRunResultMap = new java.util.HashMap<String, String>();
			threadRunResultMap.put("errorCode", null);
			threadRunResultMap.put("status", "");
			return threadRunResultMap;
		};
	};

	protected PropertiesWithType context_param = new PropertiesWithType();
	public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();

	public String status = "";

	private final org.talend.components.common.runtime.SharedConnectionsPool connectionPool = new org.talend.components.common.runtime.SharedConnectionsPool() {
		public java.sql.Connection getDBConnection(String dbDriver, String url, String userName, String password,
				String dbConnectionName) throws ClassNotFoundException, java.sql.SQLException {
			return SharedDBConnection.getDBConnection(dbDriver, url, userName, password, dbConnectionName);
		}

		public java.sql.Connection getDBConnection(String dbDriver, String url, String dbConnectionName)
				throws ClassNotFoundException, java.sql.SQLException {
			return SharedDBConnection.getDBConnection(dbDriver, url, dbConnectionName);
		}
	};

	private static final String GLOBAL_CONNECTION_POOL_KEY = "GLOBAL_CONNECTION_POOL";

	{
		globalMap.put(GLOBAL_CONNECTION_POOL_KEY, connectionPool);
	}

	private final static java.util.Properties jobInfo = new java.util.Properties();
	private final static java.util.Map<String, String> mdcInfo = new java.util.HashMap<>();
	private final static java.util.concurrent.atomic.AtomicLong subJobPidCounter = new java.util.concurrent.atomic.AtomicLong();

	public static void main(String[] args) {
		final Flatfile FlatfileClass = new Flatfile();

		int exitCode = FlatfileClass.runJobInTOS(args);
		if (exitCode == 0) {
			log.info("TalendJob: 'Flatfile' - Done.");
		}

		System.exit(exitCode);
	}

	private void getjobInfo() {
		final String TEMPLATE_PATH = "src/main/templates/jobInfo_template.properties";
		final String BUILD_PATH = "../jobInfo.properties";
		final String path = this.getClass().getResource("").getPath();
		if (path.lastIndexOf("target") > 0) {
			final java.io.File templateFile = new java.io.File(
					path.substring(0, path.lastIndexOf("target")).concat(TEMPLATE_PATH));
			if (templateFile.exists()) {
				readJobInfo(templateFile);
				return;
			}
		}
		readJobInfo(new java.io.File(BUILD_PATH));
	}

	private void readJobInfo(java.io.File jobInfoFile) {

		if (jobInfoFile.exists()) {
			try (java.io.InputStream is = new java.io.FileInputStream(jobInfoFile)) {
				jobInfo.load(is);
			} catch (IOException e) {

				log.debug("Read jobInfo.properties file fail: " + e.getMessage());

			}
		}
		log.info(String.format("Project name: %s\tJob name: %s\tGIT Commit ID: %s\tTalend Version: %s", projectName,
				jobName, jobInfo.getProperty("gitCommitId"), "8.0.1.20241016_1624-patch"));

	}

	public String[][] runJob(String[] args) {

		int exitCode = runJobInTOS(args);
		String[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };

		return bufferValue;
	}

	public boolean hastBufferOutputComponent() {
		boolean hastBufferOutput = false;

		return hastBufferOutput;
	}

	public int runJobInTOS(String[] args) {
		// reset status
		status = "";

		String lastStr = "";
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--context_param")) {
				lastStr = arg;
			} else if (lastStr.equals("")) {
				evalParam(arg);
			} else {
				evalParam(lastStr + " " + arg);
				lastStr = "";
			}
		}

		final boolean enableCBP = false;
		boolean inOSGi = routines.system.BundleUtils.inOSGi();

		if (!inOSGi) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() == null) {
				try {
					org.talend.metrics.CBPClient.startListenIfNotStarted(enableCBP, true);
				} catch (java.lang.Exception e) {
					errorCode = 1;
					status = "failure";
					e.printStackTrace();
					return 1;
				}
			}
		}

		enableLogStash = "true".equalsIgnoreCase(System.getProperty("audit.enabled"));

		if (!"".equals(log4jLevel)) {

			if ("trace".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.TRACE);
			} else if ("debug".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.DEBUG);
			} else if ("info".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.INFO);
			} else if ("warn".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.WARN);
			} else if ("error".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.ERROR);
			} else if ("fatal".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.FATAL);
			} else if ("off".equalsIgnoreCase(log4jLevel)) {
				org.apache.logging.log4j.core.config.Configurator.setLevel(log.getName(),
						org.apache.logging.log4j.Level.OFF);
			}
			org.apache.logging.log4j.core.config.Configurator
					.setLevel(org.apache.logging.log4j.LogManager.getRootLogger().getName(), log.getLevel());

		}

		getjobInfo();
		log.info("TalendJob: 'Flatfile' - Start.");

		java.util.Set<Object> jobInfoKeys = jobInfo.keySet();
		for (Object jobInfoKey : jobInfoKeys) {
			org.slf4j.MDC.put("_" + jobInfoKey.toString(), jobInfo.get(jobInfoKey).toString());
		}
		org.slf4j.MDC.put("_pid", pid);
		org.slf4j.MDC.put("_rootPid", rootPid);
		org.slf4j.MDC.put("_fatherPid", fatherPid);
		org.slf4j.MDC.put("_projectName", projectName);
		org.slf4j.MDC.put("_startTimestamp", java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
				.format(java.time.format.DateTimeFormatter.ISO_INSTANT));
		org.slf4j.MDC.put("_jobRepositoryId", "_n4_hkKwHEe-23YFR61FRHQ");
		org.slf4j.MDC.put("_compiledAtTimestamp", "2024-11-26T17:04:34.206788500Z");

		java.lang.management.RuntimeMXBean mx = java.lang.management.ManagementFactory.getRuntimeMXBean();
		String[] mxNameTable = mx.getName().split("@"); //$NON-NLS-1$
		if (mxNameTable.length == 2) {
			org.slf4j.MDC.put("_systemPid", mxNameTable[0]);
		} else {
			org.slf4j.MDC.put("_systemPid", String.valueOf(java.lang.Thread.currentThread().getId()));
		}

		if (enableLogStash) {
			java.util.Properties properties_talendJobLog = new java.util.Properties();
			properties_talendJobLog.setProperty("root.logger", "audit");
			properties_talendJobLog.setProperty("encoding", "UTF-8");
			properties_talendJobLog.setProperty("application.name", "Talend Studio");
			properties_talendJobLog.setProperty("service.name", "Talend Studio Job");
			properties_talendJobLog.setProperty("instance.name", "Talend Studio Job Instance");
			properties_talendJobLog.setProperty("propagate.appender.exceptions", "none");
			properties_talendJobLog.setProperty("log.appender", "file");
			properties_talendJobLog.setProperty("appender.file.path", "audit.json");
			properties_talendJobLog.setProperty("appender.file.maxsize", "52428800");
			properties_talendJobLog.setProperty("appender.file.maxbackup", "20");
			properties_talendJobLog.setProperty("host", "false");

			System.getProperties().stringPropertyNames().stream().filter(it -> it.startsWith("audit.logger."))
					.forEach(key -> properties_talendJobLog.setProperty(key.substring("audit.logger.".length()),
							System.getProperty(key)));

			org.apache.logging.log4j.core.config.Configurator
					.setLevel(properties_talendJobLog.getProperty("root.logger"), org.apache.logging.log4j.Level.DEBUG);

			auditLogger_talendJobLog = org.talend.job.audit.JobEventAuditLoggerFactory
					.createJobAuditLogger(properties_talendJobLog);
		}

		if (clientHost == null) {
			clientHost = defaultClientHost;
		}

		if (pid == null || "0".equals(pid)) {
			pid = TalendString.getAsciiRandomString(6);
		}

		org.slf4j.MDC.put("_pid", pid);

		if (rootPid == null) {
			rootPid = pid;
		}

		org.slf4j.MDC.put("_rootPid", rootPid);

		if (fatherPid == null) {
			fatherPid = pid;
		} else {
			isChildJob = true;
		}
		org.slf4j.MDC.put("_fatherPid", fatherPid);

		if (portStats != null) {
			// portStats = -1; //for testing
			if (portStats < 0 || portStats > 65535) {
				// issue:10869, the portStats is invalid, so this client socket can't open
				System.err.println("The statistics socket port " + portStats + " is invalid.");
				execStat = false;
			}
		} else {
			execStat = false;
		}

		try {
			java.util.Dictionary<String, Object> jobProperties = null;
			if (inOSGi) {
				jobProperties = routines.system.BundleUtils.getJobProperties(jobName);

				if (jobProperties != null && jobProperties.get("context") != null) {
					contextStr = (String) jobProperties.get("context");
				}

				if (jobProperties != null && jobProperties.get("taskExecutionId") != null) {
					taskExecutionId = (String) jobProperties.get("taskExecutionId");
				}

				// extract ids from parent route
				if (null == taskExecutionId || taskExecutionId.isEmpty()) {
					for (String arg : args) {
						if (arg.startsWith("--context_param")
								&& (arg.contains("taskExecutionId") || arg.contains("jobExecutionId"))) {

							String keyValue = arg.replace("--context_param", "");
							String[] parts = keyValue.split("=");
							String[] cleanParts = java.util.Arrays.stream(parts).filter(s -> !s.isEmpty())
									.toArray(String[]::new);
							if (cleanParts.length == 2) {
								String key = cleanParts[0];
								String value = cleanParts[1];
								if ("taskExecutionId".equals(key.trim()) && null != value) {
									taskExecutionId = value.trim();
								} else if ("jobExecutionId".equals(key.trim()) && null != value) {
									jobExecutionId = value.trim();
								}
							}
						}
					}
				}
			}

			// first load default key-value pairs from application.properties
			if (isStandaloneMS) {
				context.putAll(this.getDefaultProperties());
			}
			// call job/subjob with an existing context, like: --context=production. if
			// without this parameter, there will use the default context instead.
			java.io.InputStream inContext = Flatfile.class.getClassLoader()
					.getResourceAsStream("test_sb/flatfile_0_1/contexts/" + contextStr + ".properties");
			if (inContext == null) {
				inContext = Flatfile.class.getClassLoader()
						.getResourceAsStream("config/contexts/" + contextStr + ".properties");
			}
			if (inContext != null) {
				try {
					// defaultProps is in order to keep the original context value
					if (context != null && context.isEmpty()) {
						defaultProps.load(inContext);
						if (inOSGi && jobProperties != null) {
							java.util.Enumeration<String> keys = jobProperties.keys();
							while (keys.hasMoreElements()) {
								String propKey = keys.nextElement();
								if (defaultProps.containsKey(propKey)) {
									defaultProps.put(propKey, (String) jobProperties.get(propKey));
								}
							}
						}
						context = new ContextProperties(defaultProps);
					}
					if (isStandaloneMS) {
						// override context key-value pairs if provided using --context=contextName
						defaultProps.load(inContext);
						context.putAll(defaultProps);
					}
				} finally {
					inContext.close();
				}
			} else if (!isDefaultContext) {
				// print info and job continue to run, for case: context_param is not empty.
				System.err.println("Could not find the context " + contextStr);
			}
			// override key-value pairs if provided via --config.location=file1.file2 OR
			// --config.additional-location=file1,file2
			if (isStandaloneMS) {
				context.putAll(this.getAdditionalProperties());
			}

			// override key-value pairs if provide via command line like
			// --key1=value1,--key2=value2
			if (!context_param.isEmpty()) {
				context.putAll(context_param);
				// set types for params from parentJobs
				for (Object key : context_param.keySet()) {
					String context_key = key.toString();
					String context_type = context_param.getContextType(context_key);
					context.setContextType(context_key, context_type);

				}
			}
			class ContextProcessing {
				private void processContext_0() {
				}

				public void processAllContext() {
					processContext_0();
				}
			}

			new ContextProcessing().processAllContext();
		} catch (java.io.IOException ie) {
			System.err.println("Could not load context " + contextStr);
			ie.printStackTrace();
		}

		// get context value from parent directly
		if (parentContextMap != null && !parentContextMap.isEmpty()) {
		}

		// Resume: init the resumeUtil
		resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
		resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
		resumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);

		List<String> parametersToEncrypt = new java.util.ArrayList<String>();
		// Resume: jobStart
		resumeUtil.addLog("JOB_STARTED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "",
				"", "", "", "", resumeUtil.convertToJsonText(context, ContextProperties.class, parametersToEncrypt));

		org.slf4j.MDC.put("_context", contextStr);
		log.info("TalendJob: 'Flatfile' - Started.");
		java.util.Optional.ofNullable(org.slf4j.MDC.getCopyOfContextMap()).ifPresent(mdcInfo::putAll);

		if (execStat) {
			try {
				runStat.openSocket(!isChildJob);
				runStat.setAllPID(rootPid, fatherPid, pid, jobName);
				runStat.startThreadStat(clientHost, portStats);
				runStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);
			} catch (java.io.IOException ioException) {
				ioException.printStackTrace();
			}
		}

		java.util.concurrent.ConcurrentHashMap<Object, Object> concurrentHashMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
		globalMap.put("concurrentHashMap", concurrentHashMap);

		long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endUsedMemory = 0;
		long end = 0;

		startTime = System.currentTimeMillis();

		this.globalResumeTicket = true;// to run tPreJob

		if (enableLogStash) {
			talendJobLog.addJobStartMessage();
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		this.globalResumeTicket = false;// to run others jobs

		try {
			errorCode = null;
			tFileInputDelimited_1Process(globalMap);
			if (!"failure".equals(status)) {
				status = "end";
			}
		} catch (TalendException e_tFileInputDelimited_1) {
			globalMap.put("tFileInputDelimited_1_SUBPROCESS_STATE", -1);

			e_tFileInputDelimited_1.printStackTrace();

		}

		this.globalResumeTicket = true;// to run tPostJob

		end = System.currentTimeMillis();

		if (watch) {
			System.out.println((end - startTime) + " milliseconds");
		}

		endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (false) {
			System.out.println((endUsedMemory - startUsedMemory) + " bytes memory increase when running : Flatfile");
		}
		if (enableLogStash) {
			talendJobLog.addJobEndMessage(startTime, end, status);
			try {
				talendJobLogProcess(globalMap);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		if (execStat) {
			runStat.updateStatOnJob(RunStat.JOBEND, fatherNode);
			runStat.stopThreadStat();
		}
		if (!inOSGi) {
			if (org.talend.metrics.CBPClient.getInstanceForCurrentVM() != null) {
				s("none");
				org.talend.metrics.CBPClient.getInstanceForCurrentVM().sendData();
			}
		}

		int returnCode = 0;

		if (errorCode == null) {
			returnCode = status != null && status.equals("failure") ? 1 : 0;
		} else {
			returnCode = errorCode.intValue();
		}
		resumeUtil.addLog("JOB_ENDED", "JOB:" + jobName, parent_part_launcher, Thread.currentThread().getId() + "", "",
				"" + returnCode, "", "", "");
		resumeUtil.flush();

		org.slf4j.MDC.remove("_subJobName");
		org.slf4j.MDC.remove("_subJobPid");
		org.slf4j.MDC.remove("_systemPid");
		log.info("TalendJob: 'Flatfile' - Finished - status: " + status + " returnCode: " + returnCode);

		return returnCode;

	}

	// only for OSGi env
	public void destroy() {
		// add CBP code for OSGI Executions
		if (null != taskExecutionId && !taskExecutionId.isEmpty()) {
			try {
				org.talend.metrics.DataReadTracker.setExecutionId(taskExecutionId, jobExecutionId, false);
				org.talend.metrics.DataReadTracker.sealCounter();
				org.talend.metrics.DataReadTracker.reset();
			} catch (Exception | NoClassDefFoundError e) {
				// ignore
			}
		}

	}

	private java.util.Map<String, Object> getSharedConnections4REST() {
		java.util.Map<String, Object> connections = new java.util.HashMap<String, Object>();

		return connections;
	}

	private void evalParam(String arg) {
		if (arg.startsWith("--resuming_logs_dir_path")) {
			resuming_logs_dir_path = arg.substring(25);
		} else if (arg.startsWith("--resuming_checkpoint_path")) {
			resuming_checkpoint_path = arg.substring(27);
		} else if (arg.startsWith("--parent_part_launcher")) {
			parent_part_launcher = arg.substring(23);
		} else if (arg.startsWith("--watch")) {
			watch = true;
		} else if (arg.startsWith("--stat_port=")) {
			String portStatsStr = arg.substring(12);
			if (portStatsStr != null && !portStatsStr.equals("null")) {
				portStats = Integer.parseInt(portStatsStr);
			}
		} else if (arg.startsWith("--trace_port=")) {
			portTraces = Integer.parseInt(arg.substring(13));
		} else if (arg.startsWith("--client_host=")) {
			clientHost = arg.substring(14);
		} else if (arg.startsWith("--context=")) {
			contextStr = arg.substring(10);
			isDefaultContext = false;
		} else if (arg.startsWith("--father_pid=")) {
			fatherPid = arg.substring(13);
		} else if (arg.startsWith("--root_pid=")) {
			rootPid = arg.substring(11);
		} else if (arg.startsWith("--father_node=")) {
			fatherNode = arg.substring(14);
		} else if (arg.startsWith("--pid=")) {
			pid = arg.substring(6);
		} else if (arg.startsWith("--context_type")) {
			String keyValue = arg.substring(15);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.setContextType(keyValue.substring(0, index),
							replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.setContextType(keyValue.substring(0, index), keyValue.substring(index + 1));
				}

			}

		} else if (arg.startsWith("--context_param")) {
			String keyValue = arg.substring(16);
			int index = -1;
			if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
				if (fatherPid == null) {
					context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));
				} else { // the subjob won't escape the especial chars
					context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
				}
			}
		} else if (arg.startsWith("--context_file")) {
			String keyValue = arg.substring(15);
			String filePath = new String(java.util.Base64.getDecoder().decode(keyValue));
			java.nio.file.Path contextFile = java.nio.file.Paths.get(filePath);
			try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(contextFile)) {
				String line;
				while ((line = reader.readLine()) != null) {
					int index = -1;
					if ((index = line.indexOf('=')) > -1) {
						if (line.startsWith("--context_param")) {
							if ("id_Password".equals(context_param.getContextType(line.substring(16, index)))) {
								context_param.put(line.substring(16, index),
										routines.system.PasswordEncryptUtil.decryptPassword(line.substring(index + 1)));
							} else {
								context_param.put(line.substring(16, index), line.substring(index + 1));
							}
						} else {// --context_type
							context_param.setContextType(line.substring(15, index), line.substring(index + 1));
						}
					}
				}
			} catch (java.io.IOException e) {
				System.err.println("Could not load the context file: " + filePath);
				e.printStackTrace();
			}
		} else if (arg.startsWith("--log4jLevel=")) {
			log4jLevel = arg.substring(13);
		} else if (arg.startsWith("--audit.enabled") && arg.contains("=")) {// for trunjob call
			final int equal = arg.indexOf('=');
			final String key = arg.substring("--".length(), equal);
			System.setProperty(key, arg.substring(equal + 1));
		}
	}

	private static final String NULL_VALUE_EXPRESSION_IN_COMMAND_STRING_FOR_CHILD_JOB_ONLY = "<TALEND_NULL>";

	private final String[][] escapeChars = { { "\\\\", "\\" }, { "\\n", "\n" }, { "\\'", "\'" }, { "\\r", "\r" },
			{ "\\f", "\f" }, { "\\b", "\b" }, { "\\t", "\t" } };

	private String replaceEscapeChars(String keyValue) {

		if (keyValue == null || ("").equals(keyValue.trim())) {
			return keyValue;
		}

		StringBuilder result = new StringBuilder();
		int currIndex = 0;
		while (currIndex < keyValue.length()) {
			int index = -1;
			// judege if the left string includes escape chars
			for (String[] strArray : escapeChars) {
				index = keyValue.indexOf(strArray[0], currIndex);
				if (index >= 0) {

					result.append(keyValue.substring(currIndex, index + strArray[0].length()).replace(strArray[0],
							strArray[1]));
					currIndex = index + strArray[0].length();
					break;
				}
			}
			// if the left string doesn't include escape chars, append the left into the
			// result
			if (index < 0) {
				result.append(keyValue.substring(currIndex));
				currIndex = currIndex + keyValue.length();
			}
		}

		return result.toString();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getStatus() {
		return status;
	}

	ResumeUtil resumeUtil = null;
}
/************************************************************************************************
 * 168661 characters generated by Talend Cloud Data Fabric on the 26 November
 * 2024 at 17:04:34 GMT
 ************************************************************************************************/