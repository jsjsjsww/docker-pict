package com.neo.service;

import com.neo.domain.CTModel;
import com.neo.domain.TestSuite;



import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.io.InputStreamReader;

public class PICTMethod {
  public static void generateModelFile(CTModel model){
    StringBuffer res = new StringBuffer();
    int[] values = model.getValues();
    for (int i = 0; i < values.length; i++) {
      res.append("p" + (i + 1) + ": ");
      for (int j = 0; j < values[i]; j++) {
        res.append(j);
        if (j != values[i] -1)
          res.append(",");
	  }
	  res.append("\n");
	}
	try {
	  File file = new File("./PICT/model.txt");
	  if (!file.exists()) {
		file.getParentFile().mkdirs();
		file.createNewFile();
	  }
	  FileWriter writer = new FileWriter(file);
	  writer.write(res.toString());
	  writer.close();
	} catch (IOException e) {
	  e.printStackTrace();
	}
  }

  public static TestSuite runPICT(String modelFile, int strength){
	System.out.println(System.getProperty("user.dir"));
	String command = "PICT/pict.exe " +  modelFile + " /o:" + strength;
	Runtime runtime = Runtime.getRuntime();
	long res = 0;
	ArrayList<int[]> testsuite = new ArrayList<>();
	try{
	  Instant start = Instant.now();
	  Process p = runtime.exec(command);
	  p.waitFor();
	  Instant end = Instant.now();
	  res = Duration.between(start, end).toMillis();
	  BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
	  String s;
	  br.readLine();
	  while((s = br.readLine()) != null) {
		System.out.println(s);
		String[] tmp = s.split("\t");
		int[] ts = new int[tmp.length];
		for(int j = 0; j < ts.length; j++)
		  ts[j] = Integer.parseInt(tmp[j]);
		testsuite.add(ts);
	  }
	}catch (Exception e){
	  e.printStackTrace();
	}
	finally {
	  return new TestSuite(testsuite, res);
	}

  }



  public static void  main(String[] args){
    //generateModelFile(new CTModel(3, 2, new int[]{2, 2, 2}));
	runPICT("PICT/model.txt", 2);
  }
}
