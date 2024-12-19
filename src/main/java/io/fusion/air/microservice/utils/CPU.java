/**
 * (C) Copyright 2018 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.utils;

import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.LinkedHashMap;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author arafkarsh
 * @date July 15, 2018
 */

public class CPU {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	// https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/management/OperatingSystemMXBean.html
	private static final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
	private static final LinkedHashMap<String, Method> methodsMap = new LinkedHashMap<>();
	/**
	 * Set up the CPU Stats with all the necessary Methods
	 */
	static {
		loadMethods();
	}

	/**
	 * Load All Methods
	 */
	private static void loadMethods() {
		for (Method method : osMXBean.getClass().getDeclaredMethods()) {
			try {
				method.setAccessible(true);
				if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
					methodsMap.put(method.getName(), method);
				}
			} catch (Exception ex) {
				log.info("Error in CPU.loadMethods() : {} ", ex.getMessage());
			}
		} // Loop
	}

	/**
	 * Invoke the Method
	 * @param methodName
	 * @return
	 */
	private static Object invoke(String methodName, OperatingSystemMXBean osMXBean) {
		if(methodName == null) {
			log.info("Method {}() Invalid!", methodName);
			return "";
		}
		if(osMXBean == null) {
			log.info("OS MXBean {}() Invalid!", osMXBean);
			return "";
		}
		Object value =  Long.valueOf("0");
		Method method = methodsMap.get(methodName);
		if(method != null) {
			try {
				value = method.invoke(osMXBean);
			} catch (Exception e) {
				log.info("Exception in method {}.invoke(_osMXBean) ERROR={}", methodName,e.getMessage());
			}
		} else {
			log.debug("Method {}() Not Found!", methodName);
		}
		return value;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Prints All the CPU Stats
	 */
	public static void printAllCpuStats() {
		for(String methodName : methodsMap.keySet()) {
			Std.println(methodName+"() = "+invoke(methodName, osMXBean));
		}
	}

	// ============  All CPU Stat Public Methods =================================
	//  Memory Details -----------------------------------------------------------
	/**
	 * Get Committed Virtual Memory Size
	 * @return
	 */
	public static long getCommittedVirtualMemorySize() {
		try {	return (Long) invoke("getCommittedVirtualMemorySize", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored); }
		return 0;
	}

	/**
	 * Get Total Swap Space
	 * @return
	 */
	public static long getTotalSwapSpaceSize() {
		try {	return (Long) invoke("getTotalSwapSpaceSize", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return 0;
	}

	/**
	 * Get Free Swap Space
	 * @return
	 */
	public static long getFreeSwapSpaceSize() {
		try {	return (Long) invoke("getFreeSwapSpaceSize", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return 0;
	}

	/**
	 * Returns CPU Process Time
	 * @return
	 */
	public static int getProcessCpuTime() {
		try {	return (Integer) invoke("getProcessCpuTime", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return 0;
	}

	/**
	 * Get Free Physical Memory
	 * @return
	 */
	public static long getFreePhysicalMemorySize() {
		try {	return (Long) invoke("getFreePhysicalMemorySize", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return 0;
	}

	/**
	 * Get Total Physical Memory
	 * @return
	 */
	public static long getTotalPhysicalMemorySize() {
		try {	return (Long) invoke("getTotalPhysicalMemorySize", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return 0;
	}

	//  File Descriptor Count ----------------------------------------------------
	/**
	 * Get Open File Descriptor Count
	 * @return
	 */
	public static long getOpenFileDescriptorCount() {
		try {	return  (Long) invoke("getOpenFileDescriptorCount", osMXBean); }
		catch (Exception ignored) {
			Std.println(ignored.getMessage());
		}
		return 0;
	}

	/**
	 * Get Max File Descriptor Count
	 * @return
	 */
	public static long getMaxFileDescriptorCount() {
		try {	return  (Long) invoke("getMaxFileDescriptorCount", osMXBean); }
		catch (Exception ignored) {
			Std.println(ignored.getMessage());
		}
		return 0;
	}

	// CPU LOAD -------------------------------------------------------------------
	/**
	 * Get System CPU Load
	 * @return
	 */
	public static double getSystemCpuLoad2() {
		// Std.println("getSystemCpuLoad() <=> "+invoke("getSystemCpuLoad"));
		try {	return  (Double) invoke("getSystemCpuLoad",osMXBean); }
		catch (Exception ignored) {
			/* ignored.printStackTrace(); */}
		return 0.0;
	}

	public static Object getSystemCpuLoad() {
		try {	return  invoke("getSystemCpuLoad", osMXBean); }
		catch (Exception ignored) {
			Std.println(ignored.getMessage());
		}
		return "0.0";
	}

	/**
	 * Get Process CPU Load
	 * @return
	 */
	public static double getProcessCpuLoad2() {
		try {	return  (Double) invoke("getProcessCpuLoad", osMXBean); }
		catch (Exception ignored) {
			Std.println(ignored.getMessage());
		}
		return 0.0;
	}
	public static Object getProcessCpuLoad() {
		try {	return  invoke("getProcessCpuLoad", osMXBean); }
		catch (Exception ignored) { Std.printError(ignored);  }
		return "0.0";
	}

	/**
	 * Returns in MB
	 * @param bytes
	 * @return
	 */
	public static long toMB(long bytes) {
		return (bytes > 0) ? bytes / (1024 * 1024) : 0;
	}

	/**
	 *
	 * @param bytes
	 * @return
	 */
	public static String toMBString(long bytes) {
		long mb = toMB(bytes);
		return (mb > 1024) ? String.format("%.02f",((double)mb/1024)) + " GB" : mb + " MB";
	}

	/**
	 * Returns Free Memory
	 * @return
	 */
	public static long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns Total Memory
	 * @return
	 */
	public static long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Returns Max Memory
	 * @return
	 */
	public static long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * Returns available processors
	 * @return
	 */
	public static int availableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Prints Complete CPU Stats
	 * @return
	 */
	public static String printCpuStats() {
		StringBuilder sb = new StringBuilder();

		sb.append("|cpus=").append(availableProcessors());

		String val = "";
		try  {	val = String.format("%.02f", CPU.getProcessCpuLoad()); } catch (Exception ignored) { val = ""; }
		sb.append("|PCPU=").append(val);
		try  {	val = String.format("%.02f", CPU.getSystemCpuLoad()); } catch (Exception ignored) { val = ""; }
		sb.append("|SCPU=").append(val);

		sb.append("|FM=").append(toMBString(freeMemory()));
		sb.append("|TM=").append(toMBString(totalMemory()));

		try  {	val = String.format("%.02f", CPU.getFreePhysicalMemorySize()); } catch (Exception ignored) { val = ""; }
		sb.append("|FSMem=").append(val);
		sb.append("|TSMem=").append(toMBString(CPU.getTotalPhysicalMemorySize()));

		sb.append("|OFD=").append(CPU.getOpenFileDescriptorCount()).append("/");
		sb.append(CPU.getMaxFileDescriptorCount());

		sb.append("|FSwap=").append(toMBString(CPU.getFreeSwapSpaceSize()));
		sb.append("|TSwap=").append(toMBString(CPU.getTotalSwapSpaceSize()));

		return sb.toString();
	}

	/**
	 * Test CPU Utility
	 *
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		CPU.printAllCpuStats();

		int sleepTime = 3000;
		for(int x=0; x<7; x++) {
			Std.println(x+")> Sleeping for "+sleepTime+" ms : "+new Date());
			Thread.sleep(sleepTime);
			Std.println(new Date()+printCpuStats());
		}
		Std.println(new Date()+printCpuStats());
	}

}