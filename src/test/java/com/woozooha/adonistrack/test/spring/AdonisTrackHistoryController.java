package com.woozooha.adonistrack.test.spring;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.writer.History;

@RestController
public class AdonisTrackHistoryController {

    @GetMapping("/adonistrack/histories")
    public List<Invocation> histories() {
        History history = AdonisTrackAspect.getConfig().getHistory();
        
        List<Invocation> invocationList = history.getInvocationList();

        return invocationList;
    }

}
