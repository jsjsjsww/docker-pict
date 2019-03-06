package com.neo.controller;

import com.neo.domain.CTModel;
import com.neo.domain.TestSuite;
import com.neo.service.PICTMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DockerController {
	
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    // PICT 3.5 version
    public TestSuite PICTGeneration(HttpServletRequest request) {

        BufferedReader br;
        StringBuilder sb = null;
        String reqBody = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    request.getInputStream()));
            String line = null;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(reqBody);
        int parameters = (Integer)jsonObject.get("parameters");
        int strength = (Integer)jsonObject.get("strength");
        JSONArray jsonArray = (JSONArray)jsonObject.get("values");
        List valueList = jsonArray.toList();
        int[] values = new int[valueList.size()];
        for(int i = 0; i < values.length; i++)
            values[i] = (Integer)valueList.get(i);
        jsonArray = (JSONArray)jsonObject.get("constraint");
        List constraintList = jsonArray.toList();
        ArrayList<String> constraint = new ArrayList<>();
        for(int i = 0; i < constraintList.size(); i++){
            constraint.add((String)constraintList.get(i));
        }
        CTModel model = new CTModel(parameters, strength, values, constraint, new ArrayList<>(), new ArrayList<>());
        PICTMethod.generateModelFile(model);
        TestSuite ts = PICTMethod.runPICT("model.txt", strength);
        return ts;
    }

    @GetMapping("/check")
    public String healthCheck(){
        return "ok";
    }
}