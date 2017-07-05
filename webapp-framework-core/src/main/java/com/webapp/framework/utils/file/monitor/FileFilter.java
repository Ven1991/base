package com.webapp.framework.utils.file.monitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.DirectoryWalker.CancelException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SizeFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class FileFilter {
	private PrefixFileFilter prefixF;
	private SuffixFileFilter suffixF;
	private AgeFileFilter ageF;
	private SizeFileFilter sizeF;
	private DirectoryFileFilter directoryF;

	public static void main(String[] args) {
		FileFilter filter = new FileFilter();

		File baseDir = new File("E:/work/exchange/");

		List prefixs = new ArrayList();
		prefixs.add("rmi");
		filter.setFilePrefix(prefixs);

		List suffixs = new ArrayList();
		suffixs.add(".xml");
		suffixs.add(".properties");
		filter.setFileSuffix(suffixs);

		DirectoryFileFilter directoryF = new DirectoryFileFilter() {
			public boolean accept(File file) {
				String path = MessageFormat.format("src{0}main{0}resources", new Object[] { File.separator });
				boolean flag = file.getPath().indexOf(path) > 0;
				if (flag) {
					System.out.println(file + "\n");
				}
				return flag;
			}
		};
		filter.setDirectoryF(directoryF);

		List<File> list = filter.search(baseDir);
		System.out.println("\n found items: " + list.size());
		for (File file : list)
			System.out.println(file + ", \t\t\t\tsize:" + FileUtils.byteCountToDisplaySize(file.length()));
	}

	public List<File> search(File baseDir) {
		IOFileFilter fullConditions = new AndFileFilter(unionFilters());
		return search(baseDir, fullConditions);
	}

	public List<File> search(File baseDir, IOFileFilter fullConditions) {
		FileSearcher searcher = new FileSearcher(baseDir, fullConditions);
		Thread thread = new Thread(searcher);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return searcher.getSearchList();
	}

	public void setFilePrefix(List<String> prefix) {
		this.prefixF = new PrefixFileFilter(prefix, IOCase.SYSTEM);
	}

	public void setFileSuffix(List<String> suffix) {
		this.suffixF = new SuffixFileFilter(suffix, IOCase.SYSTEM);
	}

	public void setFileAge(Date cutoffDate) {
		this.ageF = new AgeFileFilter(cutoffDate);
	}

	public void setFileAge(Date cutoffDate, boolean acceptOlder) {
		this.ageF = new AgeFileFilter(cutoffDate, acceptOlder);
	}

	public void setFileSize(long size) {
		this.sizeF = new SizeFileFilter(size);
	}

	public void setFileSize(long size, boolean acceptLarger) {
		this.sizeF = new SizeFileFilter(size, acceptLarger);
	}

	List<IOFileFilter> unionFilters() {
		List allFilters = new ArrayList();
		if (null != this.prefixF)
			allFilters.add(this.prefixF);
		if (null != this.suffixF)
			allFilters.add(this.suffixF);
		if (null != this.ageF)
			allFilters.add(this.ageF);
		if (null != this.sizeF)
			allFilters.add(this.sizeF);
		if (null != this.directoryF) {
			allFilters.add(this.directoryF);
		}
		return allFilters;
	}

	public DirectoryFileFilter getDirectoryF() {
		return this.directoryF;
	}

	public void setDirectoryF(DirectoryFileFilter directoryF) {
		this.directoryF = directoryF;
	}

	class FileSearcher extends DirectoryWalker implements Runnable {
		private volatile boolean cancelled = false;
		private File basePath;
		private List<File> finalResult = new ArrayList();
		private long startTime;
		private long endTime;

		public FileSearcher(File basePath, IOFileFilter filter) {
			super(filter, -1);
			this.basePath = basePath;
		}

		public void run() {
			filter();
		}

		public void filter() {
			List result = new ArrayList();
			try {
				this.startTime = new Date().getTime();
				walk(this.basePath, result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.endTime = new Date().getTime();
			this.finalResult = result;
		}

		public List<File> getResult() {
			return this.finalResult;
		}

		public List<File> getSearchList() {
			long elapsed = this.endTime - this.startTime;
			System.out.println("\n time costing: " + elapsed + "ms");
			return this.finalResult;
		}

		protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
			return true;
		}

		protected void handleFile(File file, int depth, Collection results) throws IOException {
			results.add(file);
		}

		public void cancel() {
			this.cancelled = true;
		}

		protected boolean handleIsCancelled(File file, int depth, Collection results) {
			return this.cancelled;
		}

		protected void handleCancelled(File startDirectory, Collection results,
				DirectoryWalker.CancelException cancel) {
			if (this.cancelled) {
				cancel();
			}
			System.out.println("\n cancelled by external or interal thread");
			this.finalResult = ((List) results);
		}

		public long getStartTime() {
			return this.startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return this.endTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
	}
}