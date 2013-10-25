package org.apache.hadoop.mdfs.io;


import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;




public class BlockWriter{
	FileOutputStream dataOut;
	String src;

	BlockWriter(long blockId) throws FileNotFoundException,IOException{
		this((new Long(blockId)).toString());
	}


	BlockWriter(String fileName) throws FileNotFoundException,IOException{
		fileName="/tmp/MDFS/Blocks/Block-"+fileName;
		src=fileName;
		File f = new File(fileName);

		if(!f.exists()){
			f.getParentFile().mkdirs();
			f.createNewFile();
		}

		dataOut = new FileOutputStream(fileName);
		System.out.println(" Creating BlockWriter for fileName "+fileName);

	}


	public void writeBuffer(byte[] buffer,int offset,int length) throws IOException{
		System.out.println("  writing buffer offset "+ offset+" length "+length + " src "+src);
		dataOut.write(buffer,offset,length);
	}

	public void close() throws IOException{
		System.out.println(" FileOutputStream is closed for fileName "+src);
		dataOut.close();
	}

}


