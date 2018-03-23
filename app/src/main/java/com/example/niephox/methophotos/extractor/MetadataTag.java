package com.example.niephox.methophotos.extractor;

/**
 * Created by Niephox on 3/23/2018.
 */

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

/**
 * Models a particular tag within a {@link com.drew.metadata.Directory} and provides methods for obtaining its value.
 * Immutable.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("unused")
public class MetadataTag {
    private final int _tagType;
    @NotNull
    private final Directory _directory;

    public MetadataTag(int tagType, @NotNull Directory directory) {
        _tagType = tagType;
        _directory = directory;
    }

    /**
     * Gets the tag type as an int
     *
     * @return the tag type as an int
     */
    public int getTagType() {
        return _tagType;
    }

    /**
     * Gets the tag type in hex notation as a String with padded leading
     * zeroes if necessary (i.e. <code>0x100e</code>).
     *
     * @return the tag type as a string in hexadecimal notation
     */
    @NotNull
    public String getTagTypeHex() {
        return String.format("0x%04x", _tagType);
    }

    /**
     * Get a description of the tag's value, considering enumerated values
     * and units.
     *
     * @return a description of the tag's value
     */
    @Nullable
    public String getDescription() {
        return _directory.getDescription(_tagType);
    }

    /**
     * Get whether this tag has a name.
     * <p>
     * If <code>true</code>, it may be accessed via {@link #getTagName}.
     * If <code>false</code>, {@link #getTagName} will return a string resembling <code>"Unknown tag (0x1234)"</code>.
     *
     * @return whether this tag has a name
     */
    public boolean hasTagName() {
        return _directory.hasTagName(_tagType);
    }

    /**
     * Get the name of the tag, such as <code>Aperture</code>, or
     * <code>InteropVersion</code>.
     *
     * @return the tag's name
     */
    @NotNull
    public String getTagName() {
        return _directory.getTagName(_tagType);
    }

    /**
     * Get the name of the {@link com.drew.metadata.Directory} in which the tag exists, such as
     * <code>Exif</code>, <code>GPS</code> or <code>Interoperability</code>.
     *
     * @return name of the {@link com.drew.metadata.Directory} in which this tag exists
     */
    @NotNull
    public String getDirectoryName() {
        return _directory.getName();
    }

    /**
     * A basic representation of the tag's type and value.  EG: <code>[Exif IFD0] FNumber - f/2.8</code>.
     *
     * @return the tag's type and value
     */
    @Override
    @NotNull
    public String toString() {
        String description = getDescription();
        if (description == null)
            description = _directory.getString(getTagType()) + " (unable to formulate description)";
        return "[" + _directory.getName() + "] " + getTagName() + " - " + description;
    }
}

