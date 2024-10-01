package io.fusion.air.microservice.utils.algos;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert a word into the Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.isEndOfWord = true;
    }

    // Returns if the word is in the Trie
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                return false;
            }
            node = node.children[c - 'a'];
        }
        return node.isEndOfWord;
    }

    // Returns if there is any word in the Trie that starts with the given prefix
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                return false;
            }
            node = node.children[c - 'a'];
        }
        return true;
    }

    class TrieNode {
        public TrieNode[] children;
        public boolean isEndOfWord;

        public TrieNode() {
            children = new TrieNode[26];
            isEndOfWord = false;
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        System.out.println("apple = "+trie.search("apple"));   // returns true
        System.out.println("app   = "+trie.search("app"));     // returns false
        System.out.println("app   = "+trie.startsWith("app")); // returns true
        trie.insert("app");
        System.out.println("app   = "+trie.search("app"));     // returns true
    }
}
