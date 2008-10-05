package com.olliebown.beads.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * SampleManager keeps a central store for samples that are being loaded into
 * memory and can be used to organise samples into groups.
 */
public class SampleManager {

	/** The samples. */
	private static Hashtable<String, Sample> samples = new Hashtable<String, Sample>();
	/** The groups. */
	private static Hashtable<String, ArrayList<Sample>> groups = new Hashtable<String, ArrayList<Sample>>();

	/**
	 * Returns a new Sample from the given filename. If the Sample has already
	 * been loaded, it will not be loaded twice, but will simply be retrieved
	 * from the central store.
	 * 
	 * @param fn
	 *            the filename
	 * 
	 * @return the sample
	 */
	public static Sample sample(String fn) {
		Sample sample = samples.get(fn);
		if (sample == null) {
			try {
				sample = new Sample(fn);
				samples.put(fn, sample);
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				 //e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 //e.printStackTrace();
			}
		}
		return sample;
	}

	/**
	 * Generates a new group with the given group name and list of Samples to be
	 * added to the group.
	 * 
	 * @param groupName
	 *            the group name
	 * @param sampleList
	 *            the sample list
	 */
	public static void group(String groupName, Sample[] sampleList) {
		ArrayList<Sample> group;
		if (!groups.contains(groupName))
			group = new ArrayList<Sample>();
		else
			group = groups.get(groupName);
		for (int i = 0; i < sampleList.length; i++) {
			if (!group.contains(sampleList[i]))
				group.add(sampleList[i]);
		}
	}

	/**
	 * Generates a new group with the given group name and a folder that
	 * specifies where to load samples to be added to the group.
	 * 
	 * @param groupName
	 *            the group name
	 * @param folderName
	 *            the folder name
	 */
	public static void group(String groupName, String folderName) {
		String[] fileNameList = (new File(folderName)).list();
		for (int i = 0; i < fileNameList.length; i++) {
			fileNameList[i] = folderName + "/" + fileNameList[i];
		}
		group(groupName, fileNameList);
	}

	/**
	 * Generates a new group with the given group name and a list of file names
	 * to be added to the group.
	 * 
	 * @param groupName
	 *            the group name
	 * @param fileNameList
	 *            the file name list
	 */
	public static void group(String groupName, String[] fileNameList) {
		ArrayList group;
		if (!groups.contains(groupName)) {
			group = new ArrayList<Sample>();
			groups.put(groupName, group);
		} else
			group = groups.get(groupName);
		for (int i = 0; i < fileNameList.length; i++) {
			Sample sample = sample(fileNameList[i]);
			if (!group.contains(fileNameList[i]) && sample != null)
				group.add(sample);
		}
	}

	/**
	 * Gets the specified group as a Sample ArrayList.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @return the group
	 */
	public static ArrayList<Sample> getGroup(String groupName) {
		return groups.get(groupName);
	}
	
	/**
	 * Gets a random sample from the specified group.
	 * @param groupName the group
	 * @return a random Sample
	 */
	public static Sample randomFromGroup(String groupName) {
		ArrayList<Sample> group = groups.get(groupName);
		return group.get((int)(Math.random() * group.size()));
	}

	public static Sample fromGroup(String groupName, int index) {
		ArrayList<Sample> group = groups.get(groupName);
		return group.get(index % group.size());
	}
	
	/**
	 * Removes the sample.
	 * 
	 * @param sampleName
	 *            the sample name
	 */
	public static void removeSample(String sampleName) {
		samples.remove(sampleName);
	}

	/**
	 * Removes the sample.
	 * 
	 * @param sample
	 *            the sample
	 */
	public static void removeSample(Sample sample) {
		for (String str : samples.keySet()) {
			if (samples.get(str).equals(sample)) {
				removeSample(str);
				break;
			}
		}
	}

	/**
	 * Removes the specified group, without removing the samples.
	 * 
	 * @param groupName
	 *            the group name
	 */
	public static void removeGroup(String groupName) {
		groups.remove(groupName);
	}

	/**
	 * Removes the specified group, and removes all of the samples from the
	 * group.
	 * 
	 * @param groupName
	 *            the group name
	 */
	public static void destroyGroup(String groupName) {
		ArrayList<Sample> group = groups.get(groupName);
		for (int i = 0; i < group.size(); i++) {
			removeSample(group.get(i));
		}
		removeGroup(groupName);
	}
}
