package fr.an.bitwise4j.encoder.structio.helpers;

import java.util.HashMap;
import java.util.Map;

public class CounterPerStream {

    private static class Count {
        private int bitsCount;
        private int instrsCount;
        private int valueCharsCount;
        
        public void incr(int incrCountBits, int incrValueCharCount) {
            bitsCount += incrCountBits;
            instrsCount++;
            valueCharsCount += incrValueCharCount;
        }

        @Override
        public String toString() {
            return bitsCount + "b" 
                    + ((bitsCount > 8*1024)? "=" + (bitsCount/8/1024) + "ko" : "")
                    + " (inst=" + instrsCount + ", txt=" + valueCharsCount + ")";
        }

        
    }
    
    private String currStream = "";
    private Count globalCount = new Count();
    private Count currCount = new Count();
    private Map<String,Count> stream2count = new HashMap<String,Count>();

    // ------------------------------------------------------------------------

    public CounterPerStream() {
        stream2count.put("", currCount);
    }

    // ------------------------------------------------------------------------

    public String getCurrStream() {
        return currStream;
    }
    
    public void setCurrStream(String name) {
        this.currStream = name;
        this.currCount = stream2count.get(name);
        if (currCount == null) {
            this.currCount = new Count();
            stream2count.put(name, currCount);
        }
    }

    public void incr(int incrCountBits, int incrValueLen) {
        globalCount.incr(incrCountBits, incrValueLen);
        currCount.incr(incrCountBits, incrValueLen);
    }

    public int getCurrBitsCount() {
        return currCount.bitsCount;
    }
    
    public String toColumnsString() {
        return currCount.bitsCount + "/" + globalCount.bitsCount  
                + ":" + currCount.valueCharsCount + "/" + globalCount.valueCharsCount;
    }

    public String toStringAllCounters() {
        StringBuilder sb = new StringBuilder();
        sb.append(globalCount.toString() + " {");
        for(Map.Entry<String,Count> e : stream2count.entrySet()) {
            sb.append(e.getKey() + ":" + e.getValue() + ", ");
        }
        sb.append("}");
        if (sb.length() > 2) {
            sb.delete(sb.length()-2, sb.length());
        }
        return sb.toString();
    }

}
