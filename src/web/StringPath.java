package web;

import aya.exceptions.runtime.ValueError;

public class StringPath {
    private final String path;

    // Constructor
    public StringPath(String path) {
        if (path == null) {
            throw new ValueError("Path cannot be null.");
        }
        this.path = normalizePath(path);
    }

    // Check if the path is absolute
    public boolean isAbsolute() {
        return path.startsWith("/");
    }

    // Get the parent path
    public StringPath getParent() {
        if (path.equals("/") || path.isEmpty()) {
            return null; // Root or empty paths have no parent
        }

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex <= 0) {
            return new StringPath("/"); // Parent of a top-level path is root
        }

        return new StringPath(path.substring(0, lastSlashIndex));
    }

    // Join the current path with another path
    public StringPath join(StringPath other) {
        if (other == null) {
            throw new ValueError("Other path cannot be null.");
        }

        String otherPath = other.path;

        // If the other path is absolute, return it as the result
        if (other.isAbsolute()) {
            return new StringPath(otherPath);
        }

        // Ensure no duplicate slashes
        String basePath = this.path.endsWith("/") ? this.path.substring(0, this.path.length() - 1) : this.path;
        String relativePath = otherPath.startsWith("/") ? otherPath.substring(1) : otherPath;

        return new StringPath(basePath + "/" + relativePath);
    }
    
    public StringPath normalize() {
    	return new StringPath(normalizePath(this.path));
    }

    // Normalize a path (simplifies it by handling "..", ".", etc.)
    private static String normalizePath(String path) {
        String[] parts = path.split("/");
        StringBuilder normalized = new StringBuilder();
        int skip = 0;

        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i];

            if (part.isEmpty() || part.equals(".")) {
                continue;
            }

            if (part.equals("..")) {
                skip++;
            } else {
                if (skip > 0) {
                    skip--;
                } else {
                    normalized.insert(0, "/" + part);
                }
            }
        }

        String out = normalized.toString();
        
        // Don't convert to absolute path
        if (out.length() > 0) {
	        if (path.charAt(0) != '/' && out.charAt(0) == '/') {
	        	out = out.substring(1);
	        }
        }
        
        return out;
    }
    
    public String getName() {
        String str_path = normalize().toString();
        
        // Find the last separator index
        int lastSeparatorIndex = str_path.lastIndexOf('/');

        // Return the substring after the last separator, or the whole path if no separator is found
        return lastSeparatorIndex >= 0 ? path.substring(lastSeparatorIndex + 1) : path;
    }

    // Get the string representation of the path
    @Override
    public String toString() {
        return path;
    }

    // Main method to test the class
    public static void main(String[] args) {
        StringPath path1 = new StringPath("/home/user/docs");
        StringPath path2 = new StringPath("projects/file.txt");

        System.out.println("Path 1: " + path1); // Output: /home/user/docs
        System.out.println("Path 1 is absolute: " + path1.isAbsolute()); // Output: true
        System.out.println("Path 1 parent: " + path1.getParent()); // Output: /home/user

        System.out.println("Path 2: " + path2); // Output: projects/file.txt
        System.out.println("Path 2 is absolute: " + path2.isAbsolute()); // Output: false
        System.out.println("Path 2 parent: " + path2.getParent()); // Output: projects

        StringPath joinedPath = path1.join(path2);
        System.out.println("Joined Path: " + joinedPath); // Output: /home/user/docs/projects/file.txt

        StringPath absolutePath = new StringPath("/etc/config");
        System.out.println("Joined with absolute path: " + path1.join(absolutePath)); // Output: /etc/config
    }
}
