package org.apache.hadoop.mdfs.io;


import java.io.IOException;
import org.apache.hadoop.fs.FSInputStream;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.mdfs.protocol.MDFSNameSystem;
import org.apache.hadoop.mdfs.protocol.LocatedBlocks;
import org.apache.hadoop.mdfs.utils.MountFlags;
import org.apache.hadoop.mdfs.protocol.BlockInfo;
import org.apache.hadoop.mdfs.protocol.LocatedBlock;



public class MDFSInputStream extends FSInputStream {

	boolean closed = false;
	private String src;
	final private long blockSize;
	private MDFSNameSystem namesystem;
	private long fileLen;
	private long bufferSize;
	private LocatedBlocks fileBlocks;
	private long filePos;
	private long currentBlockEnd;
	private LocatedBlock currentBlock;
	private byte[] oneByteBuf = new byte[1];
	private BlockReader blockReader;




	public MDFSInputStream(MDFSNameSystem namesystem,String src,long fileLength,long blockSize,int bufLen) throws IOException{
		this.src = src;
		this.blockSize = blockSize;
		this.namesystem = namesystem;
		this.fileLen= fileLength;
		this.bufferSize=bufLen;
		this.filePos=0;
		this.currentBlockEnd=-1;

		fileBlocks= getBlockLocations(src,0,Long.MAX_VALUE);
		System.out.println(" Total number of blocks " + fileBlocks.getLocatedBlocks().size());
		System.out.println(" blockSize "+blockSize+" bufferSize "+bufferSize);
		if(fileLen != getFileLength())
			throw new IOException(" FileLength mismatch. write happened after open "+ getFileLength()+ "fileLength"+ fileLen);

	}


	protected void finalize() throws Throwable {

	}

	@Override
	public synchronized long getPos() throws IOException {
		return 0;
	}

	public synchronized boolean seekToNewSource(long targetPos) {
		return false;
	}

	@Override
	public synchronized int read() throws IOException {
		int ret = read(oneByteBuf, 0, 1 );
		return ( ret <= 0 ) ? -1 : (oneByteBuf[0] & 0xff);
	}

	@Override
	public synchronized int read(byte buf[], int off, int len) throws IOException{
		System.out.println(" Read called with buf len "+buf.length+ "with offset "+off+" length "+len);
		if(filePos >= getFileLength()){
			throw new IOException(" FilePosition exceeded fileLength");
		}
		else{
			if(filePos >currentBlockEnd){
				blockReader=blockSeekTo(filePos);
			}
			int realLen = (int) Math.min((long) len, (currentBlockEnd - filePos + 1L));
			int result=readBuffer(buf,off,realLen);
			if(result >= 0) {
				filePos += result;
			} else {
				// got a EOS from reader though we expect more data on it.
				throw new IOException("Unexpected EOS from the reader");
			}
	   		return result;		

		}

	}

	private synchronized int readBuffer(byte buf[], int off, int len) 
		                                                    throws IOException {
	
		return 0;
	}

	@Override
	public synchronized void seek(long targetPos) throws IOException {

	}

	@Override
	public void close() throws IOException {
	}

	LocatedBlocks getBlockLocations(String src, long start, long length) throws IOException {

		return namesystem.getBlockLocations(src, start, length);
	}

	private synchronized long getFileLength() {
		return (fileBlocks == null) ? 0 : fileBlocks.getFileLength();
	}


	private synchronized LocatedBlock getBlockAt(long offset,
			boolean updatePosition) throws IOException {
		if(fileBlocks == null) 
			throw new IOException("locatedBlocks is null");
		int targetBlockIdx = fileBlocks.findBlock(offset);
		if (targetBlockIdx < 0) { // block is not cached
			throw new IOException(" Target offset doesn't exist "+ offset);
		}
		LocatedBlock blk = fileBlocks.get(targetBlockIdx);
		// update current position
		if (updatePosition) {
			this.filePos = offset;
			this.currentBlockEnd = blk.getStartOffset() + blk.getBlockSize() - 1;
			this.currentBlock = blk;
		}
		return blk;
	}

	private synchronized BlockReader blockSeekTo(long target) throws IOException{
		if(target >= getFileLength()){
			throw new IOException(" FilePosition exceeded fileLength");
		}

		if ( blockReader != null ) {
			        blockReader.close(); 
		}
		LocatedBlock targetBlock = getBlockAt(target, true);
		if(target != filePos)
			throw new IOException("Wrong postion " + filePos + " expect " + target);
		long offsetIntoBlock = target - targetBlock.getStartOffset();
		long blockId = targetBlock.getBlock().getBlockId();
		String blockLoc= BlockReader.getBlockLocationInFS(blockId);
		System.out.println(" BlockLocation  of block "+blockId +" is "+ blockLoc);
		System.out.println(" OffsetIntoBlock "+offsetIntoBlock+" target "+target+" filePos "+filePos);
		
		namesystem.retrieveBlock(src,blockLoc,targetBlock.getBlock().getBlockId());






		
		return blockReader;	
		

	}


}

