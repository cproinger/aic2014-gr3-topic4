package at.tuwien.aic2014.gr3.domain;

/**
 * an ad
 */
public class Advertisment {

    byte[] picture;
    String[] tags;

    public Advertisment(byte[] picture, String[] tags){
        this.picture = picture;
        this.tags = tags;
    }

    public byte[] getImage() {
        return picture;
    }

    public void setImage(byte[] picture) {
        this.picture = picture;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String toString(){
        String tagString = tags[0] + ",";
        for (int i = 1;tags.length<(i-1);i++){
            tagString += tags[i] + ",";
        }
        tagString += tags[tags.length-1];
        return "Advertisment [Imagesize: " + picture.length + "tags="
                + tagString + "]";
    }
}
