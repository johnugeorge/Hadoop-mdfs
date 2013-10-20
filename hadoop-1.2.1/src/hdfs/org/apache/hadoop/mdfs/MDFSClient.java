package org.apache.hadoop.mdfs;

import java.io.IOException;
import java.net.URI;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.io.OutputStream;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.util.Progressable;

import org.apache.hadoop.fs.permission.FsPermission;

class MDFSClient {

	private short defaultReplication;
	private boolean clientRunning;
	private MDFSNameSystem namesystem;

	public MDFSClient(Configuration conf) {
	}

	private String pathString(Path path) {
		return path.toUri().getPath();
	}

	void initialize(URI uri, Configuration conf) throws IOException {

		clientRunning=true;
		this.namesystem = MDFSNameSystem.getInstance(conf);
	}

	private void checkOpen() throws IOException{
		if(!clientRunning)
			throw(new IOException("FileSystem Closed"));
	}

	OutputStream create(Path path,int flags,FsPermission permission,boolean createParent, short replication, long blockSize,Progressable progress,int bufferSize) throws IOException {
		if (permission == null) {
			permission = FsPermission.getDefault();
		}

		MDFSOutputStream result= new MDFSOutputStream(namesystem,pathString(path),flags,permission, createParent,replication,blockSize,progress,bufferSize);
		return result;

	}


	int open(Path path, int flags, int mode) throws IOException {

		return 0;
	}



	MDFSFileStatus lstat(Path path) throws IOException {
		MDFSFileStatus stat= namesystem.lstat(pathString(path));
		return stat;
	}

	void rmdir(Path path, boolean isDir) throws IOException {

		namesystem.delete(pathString(path),isDir);
	}


	void rename(Path src, Path dst) throws IOException {
	}

	String[] listdir(Path path) throws IOException {
		return namesystem.listDir(pathString(path));
	}

	void mkdirs(Path path, FsPermission permission) throws IOException {
		if (permission == null) {
			permission = FsPermission.getDefault();
		}
		namesystem.mkdirs(pathString(path),permission,false);
	}

	void close(int fd) throws IOException {
	}

	void chmod(Path path, int mode) throws IOException {
	}

	void shutdown() throws IOException {
	}

	short getDefaultReplication() {
		return defaultReplication;
	}

	void setattr(Path path, MDFSFileStatus stat, int mask) throws IOException {
	}

	long lseek(int fd, long offset, int whence) throws IOException {
		return 0 ;
	}

	int write(int fd, byte[] buf, long size, long offset) throws IOException {
		return 0 ;
	}

	int read(int fd, byte[] buf, long size, long offset) throws IOException {
		return 0 ;
	}
}