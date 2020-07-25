// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** 
Algorithm:
    1. Pick time ranges from events where attendees overlap with meeting request.
    2. Now find the non overlapping time ranges which have duration more than request duration.
*/
public final class FindMeetingQuery {
    
    private static final int END_OF_DAY = 24 * 60; 
    
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {        
        List<String> requestedAttendees = request.getAttendees().stream()
                                                 .collect(Collectors.toList());
        
        ArrayList<TimeRange> overlappingRanges = calculateOverlappingRanges(events, requestedAttendees);
        
        long meetingDuration = request.getDuration();
        
        return getNonOverlappingTimeRanges(overlappingRanges, meetingDuration);
    }

    /** 1. Calculate overlapping ranges by iterating in all the events and finding common attendees.*/
    private ArrayList<TimeRange> calculateOverlappingRanges(Collection<Event> events, List<String> requestedAttendees) {  
        ArrayList<TimeRange> overlappingRanges = new ArrayList<>();

        for (Event currentEvent: events) {
            Set<String> currentEventAttendees = currentEvent.getAttendees();
            List<String> overlapRange = currentEventAttendees.stream()
                                                             .filter(requestedAttendees::contains)
                                                             .collect(Collectors.toList());

            if (overlapRange.size() > 0) {
                overlappingRanges.add(currentEvent.getWhen());
            }
        }

        return overlappingRanges;
    }

    /** 2. Calculate non overlapping ranges by iterating in overlappingRanges in the ascending order. */
    private ArrayList<TimeRange> getNonOverlappingTimeRanges(ArrayList<TimeRange> overlappingRanges, long meetingDuration) {
        ArrayList<TimeRange> requiredRanges = new ArrayList<>();
        overlappingRanges.sort(TimeRange.ORDER_BY_START);

        int previousEnd = 0;
        for (TimeRange currentRange: overlappingRanges) {
            int currentStart = currentRange.start();
            int currentEnd = currentRange.end();
            int currentDuration = currentStart - previousEnd;

            if (meetingDuration <= currentDuration) {
                TimeRange validTimeRange = TimeRange.fromStartDuration(previousEnd, currentDuration);
                requiredRanges.add(validTimeRange);
            }       

            if (previousEnd < currentEnd) {
                previousEnd = currentEnd;
            }
        }
        
        int lastDuration = END_OF_DAY - previousEnd;

        if (meetingDuration <= lastDuration) {
            TimeRange validTimeRange = TimeRange.fromStartDuration(previousEnd, lastDuration);
            requiredRanges.add(validTimeRange);
        }

        return requiredRanges;
    }
}
