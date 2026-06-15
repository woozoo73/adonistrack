package com.woozooha.adonistrack.test.spring;

import java.util.ArrayList;
import java.util.List;

import com.woozooha.adonistrack.aspect.ProfileAspect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.writer.History;

@RestController
public class AdonisTrackHistoryController {

    @GetMapping("/adonis-track/invocations")
    public List<Invocation.InvocationSummary> getInvocations() {
        History history = ProfileAspect.getConfig().getHistory();

        List<Invocation> invocations = history.getInvocationList();

        List<Invocation.InvocationSummary> summaries = new ArrayList<Invocation.InvocationSummary>();
        for (Invocation i : invocations) {
            summaries.add(new Invocation.InvocationSummary(i));
        }

        return summaries;
    }

    @GetMapping("/adonis-track/invocations/{id}")
    public Invocation getInvocation(@PathVariable String id) {
        History history = ProfileAspect.getConfig().getHistory();

        List<Invocation> invocations = history.getInvocationList();

        for (Invocation i : invocations) {
            if (id != null ? id.equals(i.getId()) : i.getId() == null) {
                return i;
            }
        }
        return null;
    }

}
