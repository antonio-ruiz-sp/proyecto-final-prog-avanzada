package util.file;

import java.io.File;
import model.worker_manager.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.json.JsonUtil;

public class Partition {
    private static Logger logger = LogManager.getLogger(Partition.class);
    String partName;
    int begin;
    int end;
    Manager mgr;
    File origFile;
    String destfileName;
    String regex;

    public Partition(String partName, int begin, int end, Manager m, File origFile, String destFileName, String regex) {
        this.partName = partName;
        this.begin = begin;
        this.end = end;
        this.mgr = m;
        this.origFile = origFile;
        this.destfileName = destFileName;
        this.regex = regex;
    }
    
    public Partition(String partName, int begin, int end, File origFile, String destFileName, String regex) {
        this.partName = partName;
        this.begin = begin;
        this.end = end;
        //this.mgr = m;
        this.origFile = origFile;
        this.destfileName = destFileName;
        this.regex = regex;
        
    }
    
    @Override
    public String toString(){
        /*
        StringBuilder sb = new StringBuilder();
        sb.append("partition id: ").append(partName).append("\n");
        sb.append("       begin:").append(begin).append("\n");
        sb.append("         end:").append(end).append("\n");
        sb.append("destFileName: ").append(destfileName).append("\n");
        return sb.toString();*/
        return JsonUtil.toJsonRepresentation(this);
    }

    void startWork() {
        //logger.debug("Entering startWork... for "+ this.partName);
        mgr.submitTask(this);
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Manager getMgr() {
        return mgr;
    }

    public void setMgr(Manager m) {
        this.mgr = m;
    }

    public File getOrigFile() {
        return origFile;
    }

    public void setOrigFile(File origFile) {
        this.origFile = origFile;
    }

    public String getDestfileName() {
        return destfileName;
    }

    public void setDestfileName(String destfileName) {
        this.destfileName = destfileName;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
    
}
