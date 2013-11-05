package org.apache.hadoop.mdfs;

import java.io.IOException;
import java.net.URI;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.io.OutputStream;
import java.net.InetSocketAddress;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.util.Progressable;

import org.apache.hadoop.mdfs.protocol.MDFSNameSystem;
import org.apache.hadoop.mdfs.protocol.MDFSFileStatus;
import org.apache.hadoop.mdfs.protocol.MDFSProtocol;
import org.apache.hadoop.mdfs.io.MDFSOutputStream;
import org.apache.hadoop.mdfs.io.MDFSInputStream;

import org.apache.hadoop.ipc.RPC;


class MDFSClient {

	private short defaultReplication;
	private boolean clientRunning;
	private MDFSProtocol namesystem;
	private Configuration conf;

	public MDFSClient(Configuration conf) {
	}

	private String pathString(Path path) {
		return path.toUri().getPath();
	}

	void initialize(URI uri, Configuration conf) throws IOException {

		clientRunning=true;
		//this.namesystem = MDFSNameSystem.getInstance(conf);
		InetSocketAddress nodeAddr = MDFSNameSystem.getAddress(conf);
		System.out.println(" Going to connect to MDFSNameSystem ");

		this.namesystem =  (MDFSProtocol) 
			RPC.waitForProxy(MDFSProtocol.class,
					MDFSProtocol.versionID,
					nodeAddr, 
					conf);
		System.out.println(" Connected to MDFSNameSystem ");
		this.conf=conf;
	}

	private void checkOpen() throws IOException{
		if(!clientRunning)
			throw(new IOException("FileSystem Closed"));
	}

	OutputStream create(Path path,int flags,FsPermission permission,boolean createParent, short replication, long blockSize,Progressable progress,int bufferSize) throws IOException {
		if (permission == null) {
			permission = FsPermission.getDefault();
		}

		MDFSOutputStream result= new MDFSOutputStream(namesystem,conf,pathString(path),flags,permission, createParent,replication,blockSize,progress,bufferSize);
		return result;

	}


	MDFSInputStream open(Path path, long length,long blockSize,int bufferSize) throws IOException {

	
		MDFSInputStream result = new MDFSInputStream(namesystem,conf,pathString(path),length,blockSize,bufferSize);

		return result;
	}



	MDFSFileStatus lstat(Path path) throws IOException {
		MDFSFileStatus stat= namesystem.lstat(pathString(path));
		return stat;
	}

	void rmdir(Path path, boolean isDir) throws IOException {

		namesystem.delete(pathString(path),isDir);
	}


	void rename(Path src, Path dst) throws IOException {
		System.out.println(" src "+ pathString(src) +" dst " +pathString(dst) );
		namesystem.rename(pathString(src),pathString(dst));
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
		clientRunning=false;
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

	
}
