package mx.kenzie.autodoc.internal;


import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringReader implements Iterable<Character> {
    public final char[] characters;
    protected final String string; // internal copy
    protected transient volatile int position;
    
    public StringReader(String string) {
        this(string.toCharArray());
    }
    
    public StringReader(char[] characters) {
        this.characters = characters;
        this.string = new String(characters);
    }
    
    public synchronized String readRest() {
        StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            builder.append(this.characters[this.position]);
        }
        return builder.toString();
    }
    
    public boolean canRead() {
        return this.position < this.characters.length && this.position >= 0;
    }
    
    public String readWord() {
        if (!this.canRead()) return "";
        final StringBuilder builder = new StringBuilder();
        char c;
        do {
            builder.append(this.current());
            if (this.canRead()) this.rotate();
        } while (this.canRead()
            && (c = this.characters[position]) != ' '
            && c != '\n'
            && c != '\t'
            && c != '\r'
        );
        return builder.toString();
    }
    
    public char current() {
        if (this.canRead()) return this.characters[this.position];
        else throw new RuntimeException("Limit exceeded.");
    }
    
    public char rotate() {
        if (this.canRead()) return this.characters[this.position++];
        else throw new RuntimeException("Limit exceeded!");
    }
    
    public String trim() {
        final StringBuilder builder = new StringBuilder();
        char c;
        while (this.canRead() && ((c = this.characters[position]) == ' '
            || c == '\n'
            || c == '\t'
            || c == '\r'
        )) builder.append(this.rotate());
        return builder.toString();
    }
    
    public int indexOf(char c) {
        return string.indexOf(c, position);
    }
    
    public int indexOf(String string) {
        return this.string.indexOf(string, position);
    }
    
    public String read(int length) {
        int end = this.position + length;
        StringBuilder builder;
        for (builder = new StringBuilder(); this.position < end && this.position < this.characters.length; ++this.position) {
            builder.append(this.characters[this.position]);
        }
        return builder.toString();
    }
    
    public synchronized String readUntil(char c) {
        final StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            char test = this.characters[this.position];
            if (c == test) break;
            builder.append(test);
        }
        return builder.toString();
    }
    
    public synchronized String readUntil(String string) {
        final StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            char test = this.characters[this.position];
            if (this.string.startsWith(string, this.position)) break;
            builder.append(test);
        }
        return builder.toString();
    }
    
    public synchronized String readUntilEscape(char c) {
        final StringBuilder builder = new StringBuilder();
        for (boolean ignore = false; this.canRead(); ++this.position) {
            final char test = this.characters[this.position];
            if (ignore) ignore = false;
            else if (test == '\\') ignore = true;
            else if (c == test) break;
            builder.append(test);
        }
        return builder.toString();
    }
    
    public synchronized String readUntilMatches(Function<String, Boolean> function) {
        final StringBuilder builder = new StringBuilder();
        while (this.canRead()) {
            final char test = this.characters[this.position];
            builder.append(test);
            ++this.position;
            if (function.apply(builder.toString())) break;
        }
        return builder.toString();
    }
    
    public synchronized String readUntilMatches(Pattern pattern) {
        final StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            final char test = this.characters[this.position];
            builder.append(test);
            if (pattern.matcher(builder.toString()).matches()) break;
        }
        return builder.toString();
    }
    
    public synchronized String readUntilMatchesAfter(Pattern pattern, char end) {
        final StringBuilder builder = new StringBuilder();
        for (boolean canEnd = false; this.canRead(); ++this.position) {
            final char test = this.characters[this.position];
            if (test == end) canEnd = true;
            if (canEnd && pattern.matcher(builder.toString()).matches()) break;
            builder.append(test);
        }
        return builder.toString();
    }
    
    public boolean hasApproaching(int index) {
        return this.remaining().length > index;
    }
    
    public char[] remaining() {
        return Arrays.copyOfRange(this.characters, this.position, this.characters.length);
    }
    
    public String remainingString() {
        return new String(Arrays.copyOfRange(this.characters, this.position, this.characters.length));
    }
    
    public char getApproaching(int index) {
        return this.characters[position + index];
    }
    
    public boolean hasNext() {
        return this.position < this.characters.length - 1;
    }
    
    public synchronized void skip() {
        if (this.canRead()) ++this.position;
    }
    
    public synchronized void skip(int i) {
        this.position += i;
    }
    
    public synchronized void rotateBack(int i) {
        this.position -= i;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(int i) {
        this.position = i;
    }
    
    public int length() {
        return this.characters.length;
    }
    
    public char previous() {
        if (this.position - 1 >= 0) return this.characters[this.position - 1];
        else throw new RuntimeException("Limit exceeded!");
    }
    
    public char next() {
        if (this.position + 1 < this.characters.length) return this.characters[this.position + 1];
        else throw new RuntimeException("Limit exceeded!");
    }
    
    public void reset() {
        this.position = 0;
    }
    
    public int charCount(char c) {
        int i = 0;
        char[] var3 = this.characters;
        int var4 = var3.length;
        
        for (char ch : var3) {
            if (ch == c) {
                ++i;
            }
        }
        
        return i;
    }
    
    @NotNull
    public Iterator<Character> iterator() {
        return new Iterative();
    }
    
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public StringReader clone() {
        StringReader reader = new StringReader(this.characters);
        reader.position = this.position;
        return reader;
    }
    
    public String toString() {
        return string;
    }
    
    protected class Iterative implements Iterator<Character> {
        final int size;
        volatile int cursor;
        volatile int lastRet = -1;
        
        Iterative() {
            this.size = StringReader.this.characters.length;
        }
        
        public boolean hasNext() {
            return this.cursor != StringReader.this.characters.length;
        }
        
        public synchronized Character next() {
            this.checkForComodification();
            int i = this.cursor;
            if (i >= StringReader.this.characters.length) {
                throw new NoSuchElementException();
            } else {
                this.cursor = i + 1;
                return StringReader.this.characters[this.lastRet = i];
            }
        }
        
        public void remove() {
            throw new ConcurrentModificationException();
        }
        
        public synchronized void forEachRemaining(Consumer<? super Character> consumer) {
            Objects.requireNonNull(consumer);
            final int size = StringReader.this.characters.length;
            int i = this.cursor;
            if (i < size) {
                while (i != size) {
                    consumer.accept(StringReader.this.characters[i++]);
                }
                this.cursor = i;
                this.lastRet = i - 1;
                this.checkForComodification();
            }
        }
        
        final void checkForComodification() {
        }
    }
}
