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
	ArrayList<String> constraint = model.getConstraint();
	int[] valueSum = new int[values.length];
	valueSum[0] = 0;
	for(int i = 1; i < valueSum.length; i++)
	  valueSum[i] = valueSum[i - 1] + values[i - 1];
	for(int i = 0; i < constraint.size(); i++){
	  res.append(transfer(constraint.get(i), valueSum));
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

  public static String transfer(String s, int[] value){
	String res = "";
	String[] split = s.split(" - ");
	split[0] = split[0].substring(2, split[0].length());
	int[] a = new int[split.length];
	for(int i = 0; i < split.length; i++) {
	  a[i] = Integer.parseInt(split[i]);
	}
	int i = 0, j = 0;
	while(i < a.length) {
	  //System.out.println("a[i] = " + a[i] + " value[j] = " + j);
	  while (j < value.length && a[i] >= value[j])
		j++;
	  //System.out.println(" j = "+j  );
	  split[i] = "[p"+ j + "]=" + (a[i] - value[j - 1]);
	  i++;
	}
	for(i = 0; i < split.length; i++) {
	  String tmp = "if ";
	  for (j = 0; j < split.length; j++) {
		if (i != j)
		  tmp += split[j] + "AND";
	  }
	  tmp = tmp.substring(0, tmp.length() - 3);
	  tmp += " then " + split[i].substring(0,split[i].indexOf('=')) + "<>" + split[i].substring(split[i].indexOf('=') + 1, split[i].length()) + ";";
	  res += tmp + "\n";
	}
	System.out.println(res);
	return res;
  }



  public static void  main(String[] args){
    //generateModelFile(new CTModel(3, 2, new int[]{2, 2, 2}));
	runPICT("PICT/model.txt", 2);
  }
}
