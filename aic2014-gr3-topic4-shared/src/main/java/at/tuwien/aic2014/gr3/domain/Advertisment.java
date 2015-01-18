package at.tuwien.aic2014.gr3.domain;

import java.util.List;

/**
 * an ad
 */
public class Advertisment {

    String filepath;
    List<String> tags;

    public Advertisment(String filepath, List<String> tags){
        this.filepath = filepath;
        this.tags = tags;
    }

    public String getUrl() {
        return filepath;
    }

    public void setUrl(String filepath) {
        this.filepath = filepath;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setSingleTag(String tag){
        this.tags.add(tag);
    }

    public String getSingleTag(int index){
        return this.tags.get(index);
    }

    public String toString(){
        String tagString = tags.get(0) + ",";
        for (int i = 1;tags.size()<(i-1);i++){
            tagString += tags.get(i) + ",";
        }
        tagString += tags.get(tags.size()-1);
        return "Advertisment [URL=" + this.filepath + ", tags="
                + tagString + "]";
    }
}
