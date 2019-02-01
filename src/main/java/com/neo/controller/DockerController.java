package com.neo.controller;

import com.neo.domain.CTModel;
import com.neo.domain.TestSuite;
import com.neo.service.PICTMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/generation")
public class DockerController {
	
    @RequestMapping(value = "/PICT", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    // PICT 3.5 version
    public TestSuite ACTSGeneration(HttpServletRequest request) {
        String p = request.getParameter("parameters");
        String stren = request.getParameter("strength");
        String value = request.getParameter("values");
        int parameters = Integer.parseInt(p);
        int strength = Integer.parseInt(stren);
        String[] val = value.split(" ");
        int[] values = new int[val.length];
        for(int i = 0; i < values.length; i++)
            values[i] = Integer.parseInt(val[i]);
        CTModel model = new CTModel(parameters, strength, values);
        PICTMethod.generateModelFile(model);
        TestSuite ts = PICTMethod.runPICT("PICT/model.txt", strength);
        return ts;
    }
}