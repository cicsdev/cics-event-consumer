/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* cics-event-consumer                                                    */
/*                                                                        */
/* (c) Copyright IBM Corp. 2012 - 2024 All Rights Reserved                */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

package com.ibm.cics.ca1y;

import com.ibm.jzos.MvsJobSubmitter;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to submit an MVS batch job.
 * 
 * @author Mark Cocker <mark_cocker@uk.ibm.com>
 * @version 1.0
 * @since 2013-09-02
 */
public class MVSJob implements EmitAdapter {

	/**
	 * Copyright statement to be included in the compiled class.
	 */
	static final String COPYRIGHT = "Licensed Materials - Property of IBM. "
			+ "CICS SupportPac CA1Y (c) Copyright IBM Corporation 2012 - 2016. All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corporation";

	/**
	 * Logging
	 */
	private static Logger logger = Logger.getLogger(MVSJob.class.getName());

	/**
	 * Property to specify the MVS job content.
	 */
	private static final String MVSJOB_CONTENT = "mvsjob.content";

	/**
	 * Property to set the MVS job id to.
	 */
	private static final String MVSJOB_JOBID = "mvsjob.jobid";
	
	/**
	 * The Job ID available once the job has been submitted.
	 */
	private String jobID;
	private String jobName;

	/**
	 * Properties from which to find the job to be submitted.
	 */
	private EmitProperties props;

	public MVSJob(EmitProperties p) {
		props = p;
	}

	public boolean send() {
		String mvsjobContent = props.getProperty(MVSJOB_CONTENT);
		MvsJobSubmitter jobSubmitter;

		if (mvsjobContent == null) {
			return false;
		}

		try {
			jobSubmitter = new MvsJobSubmitter();

		} catch (IOException _ex) {
			logger.warning(Emit.messages.getString("UnableToOpenInternalReader") + " - " + getMessageSummary());
			return false;
		}

		try {
			Scanner scanner = new Scanner(mvsjobContent);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (jobName == null) {
					StringTokenizer tok = new StringTokenizer(line);
					String jobToken = tok.nextToken();

					if (jobToken.startsWith("//")) {
						jobName = jobToken.substring(2);
					} else {
						jobName = "";
					}
				}

				jobSubmitter.write(line);
			}

			scanner.close();
			jobSubmitter.close();
			jobID = jobSubmitter.getJobid();

		} catch (Exception e) {
			logger.warning(Emit.messages.getString("MVSJOB_LOG_FAIL_MESSAGE") + " - " + getMessageSummary());
			e.printStackTrace();
			return false;
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(Emit.messages.getString("MVSJOB_LOG_SUCCESS_MESSAGE") + " - " + getMessageSummary());
		}
		
		/* Put the job id into the properties and return it to the caller in container specified by MVSJOB_JOBID */
		props.put(MVSJOB_JOBID, jobID);
		props.setPropertyReturnContainer(MVSJOB_JOBID, MVSJOB_JOBID);

		return true;
	}

	/**
	 * Return true if we should attempt to emit 
	 * 
	 * @return true if the entries in EmitProperties are valid for emission. 
	 */
	public static boolean validForEmission(EmitProperties props) {
		return props.containsKey(MVSJob.MVSJOB_CONTENT);
	}

	/**
	 * Return a short summary of the MVS job.
	 * 
	 * @return short summary of the MVS job.
	 */
	private String getMessageSummary() {
		return "job name:" + jobName + ",job ID:" + jobID;
	}
}