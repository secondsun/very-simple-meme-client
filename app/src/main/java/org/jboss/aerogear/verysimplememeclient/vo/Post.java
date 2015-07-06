/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.aerogear.verysimplememeclient.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


/**
 *
 * @author summers
 */

public class Post implements Serializable{
    
    private String topComment;
    private String bottomComment;
    
    @RecordId
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    

    private RedHatUser owner;
    

    private String fileUrl;
    private String comment;
    

    private Date posted;
    
    public RedHatUser getOwner() {
        return owner;
    }

    public void setOwner(RedHatUser owner) {
        this.owner = owner;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }


    public String getTopComment() {
        return topComment;
    }

    public void setTopComment(String topComment) {
        this.topComment = topComment;
    }

    public String getBottomComment() {
        return bottomComment;
    }

    public void setBottomComment(String bottomComment) {
        this.bottomComment = bottomComment;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.topComment);
        hash = 53 * hash + Objects.hashCode(this.bottomComment);
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.owner);
        hash = 53 * hash + Objects.hashCode(this.fileUrl);
        hash = 53 * hash + Objects.hashCode(this.comment);
        hash = 53 * hash + Objects.hashCode(this.posted);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Post other = (Post) obj;
        if (!Objects.equals(this.topComment, other.topComment)) {
            return false;
        }
        if (!Objects.equals(this.bottomComment, other.bottomComment)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }

        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.posted, other.posted)) {
            return false;
        }
        return true;
    }

    
    
}
