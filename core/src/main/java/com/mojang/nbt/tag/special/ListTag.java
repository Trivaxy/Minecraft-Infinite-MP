package com.mojang.nbt.tag.special;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListTag<T extends Tag> extends Tag implements List<T> {
    private List<T> tagList = new ArrayList<>();
    private byte tagType;

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        if (this.tagList.size() > 0) {
            this.tagType = this.tagList.get(0).getId();
        } else {
            this.tagType = 1;
        }

        dataio.writeByte(this.tagType);
        dataio.writeInt(this.tagList.size());

        for (int index = 0; index < this.tagList.size(); index++) {
            this.tagList.get(index).writeContents(dataio);
        }
    }

    @Override
    public void readContents(DataInput dataInput1) throws IOException {
        this.tagType = dataInput1.readByte();
        int listSize = dataInput1.readInt();
        this.tagList = new ArrayList<>();

        for (int index = 0; index < listSize; index++) {
            Tag tag = Tag.getTagType(this.tagType);
            tag.readContents(dataInput1);
            this.tagList.add((T)tag);
        }
    }

    @Override
    public byte getId() {
        return 9;
    }

    @Override
    public String toString() {
        return "" + this.tagList.size() + " entries of type " + Tag.getTagName(this.tagType);
    }

    public T get(int index) {
        return this.tagList.get(index);
    }

    public CompoundTag tagAt(int index) {
        if (index >= 0 && index < this.tagList.size()) {
            Tag tag = this.tagList.get(index);
            return tag.getId() == 10 ? (CompoundTag)tag : new CompoundTag();
        } else {
            return new CompoundTag();
        }
    }

    @Override
    public int size() {
        return this.tagList.size();
    }

    public ListTag<T> copy() {
        ListTag<T> shit = new ListTag<ListTag<T>>().setKey(this.getKey());
        shit.tagType = this.tagType;

        for (T t : this.tagList) {
            T cope = (T)t.copy();
            shit.tagList.add(cope);
        }

        return shit;
    }

    public T remove(int index) {
        return this.tagList.remove(index);
    }

    @Override
    public boolean isEmpty() {
        return this.tagList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.tagList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.tagList.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.tagList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return (T[])this.tagList.toArray(array);
    }

    public boolean add(T tag) {
        this.tagType = tag.getId();
        return this.tagList.add(tag);
    }

    @Override
    public boolean remove(Object obj) {
        return this.tagList.remove(obj);
    }

    @Override
    public boolean containsAll(Collection<?> col) {
        return this.tagList.containsAll(col);
    }

    @Override
    public boolean addAll(Collection<? extends T> col) {
        return this.tagList.addAll(col);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> col) {
        return this.tagList.addAll(index, col);
    }

    @Override
    public boolean removeAll(Collection<?> col) {
        return this.tagList.removeAll(col);
    }

    @Override
    public boolean retainAll(Collection<?> col) {
        return this.tagList.retainAll(col);
    }

    @Override
    public void clear() {
        this.tagList.clear();
    }

    public T set(int index, T element) {
        this.tagType = element.getId();
        return this.tagList.set(index, element);
    }

    public void add(int index, T element) {
        this.tagType = element.getId();
        this.tagList.add(index, element);
    }

    @Override
    public int indexOf(Object obj) {
        return this.tagList.indexOf(obj);
    }

    @Override
    public int lastIndexOf(Object obj) {
        return this.tagList.lastIndexOf(obj);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.tagList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.tagList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.tagList.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && this.tagList.equals(((ListTag)other).tagList);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }
}
