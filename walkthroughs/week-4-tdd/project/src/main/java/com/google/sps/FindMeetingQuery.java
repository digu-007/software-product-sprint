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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/** 
Algorithm:
    1. Pick time ranges from events where attendees overlap with meeting request.
    2. Now find the non overlapping time ranges which have duration more than request duration.
*/
public final class FindMeetingQuery {
    
    private long meetingDuration;
    private ArrayList<TimeRange> overlappingRanges;
    private Collection<String> requestedAttendees;
    private Collection<TimeRange> requiredRanges;
    
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        overlappingRanges = new ArrayList<>();
        
        requestedAttendees = request.getAttendees();
        calculateOverlappingRanges(events);
        
        meetingDuration = request.getDuration();
        requiredRanges = new ArrayList<>();
        getNonOverlappingTimeRanges();

        return requiredRanges;
    }

    /** 1. Calculate overlapping ranges by iterating in all the events and finding common attendees.*/
    private void calculateOverlappingRanges(Collection<Event> events) {  
        for (Event currentEvent: events) {
            Set<String> currentEventAttendees = currentEvent.getAttendees();
            boolean overlapRange = false;

            for (String requestedAttendee: requestedAttendees) {
                if (currentEventAttendees.contains(requestedAttendee)) {
                    overlapRange = true;
                    break;
                }
            }

            if (overlapRange) {
                overlappingRanges.add(currentEvent.getWhen());
            }
        }
    }

    /** 2. Calculate non overlapping ranges by iterating in overlappingRanges in the ascending order. */
    private void getNonOverlappingTimeRanges() {
        overlappingRanges.sort(TimeRange.ORDER_BY_START);

        int previousEnd = 0;
        for (TimeRange currentRange: overlappingRanges) {
            int currentStart = currentRange.start();
            int currentEnd = currentRange.end();
            int currentDuration = currentStart - previousEnd;

            addRangeIfValid(currentDuration, previousEnd);           

            if (previousEnd < currentEnd) {
                previousEnd = currentEnd;
            }
        }
        
        int endOfDay = 24 * 60;
        int currentDuration = endOfDay - previousEnd;

        addRangeIfValid(currentDuration, previousEnd);
    }

    /** Adds a time range if its length is atleast equal to meetingDuration. */
    private void addRangeIfValid(int duration, int previousEnd) {
        if (meetingDuration > duration) {
            return;
        }

        TimeRange validTimeRange = TimeRange.fromStartDuration(previousEnd, duration);
        requiredRanges.add(validTimeRange);
    }
}
