package firstproject;
import java.io.*;
import java.util.*;

public class Autofill {

    public static void main(String[] args) throws IOException {
        // Initialize Trie
        Trie trieDS = new Trie();
        
        // Load words from a file and insert into the Trie
        loadWordsFromFile(trieDS, "C:\\Users\\yashs\\OneDrive\\Desktop\\wordstxt.txt");  // Update with correct path
        
        // Load user's search history from a file (including frequencies)
        Map<String, Integer> userHistory = loadUserHistoryWithFrequency("C:\\Users\\yashs\\OneDrive\\Desktop\\userhistory.txt"); // User's history file
        
        // Insert history words into the Trie
        for (String word : userHistory.keySet()) {
            trieDS.insertWord(word);
        }
        
        // Take dynamic input for word prefix
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a word prefix for suggestions:");
        String prefix = scanner.nextLine();
        
        // Get autofill suggestions based on the prefix
        List<String> suggestions = trieDS.advanceSearch(prefix);
        
        // Customize suggestions based on user's history (priority given to frequent history words)
        suggestions = customizeSuggestions(suggestions, userHistory);
        
        // If there are suggestions, ask the user to select the correct one
        if (!suggestions.isEmpty()) {
            System.out.println("Suggestions: " + suggestions);
            System.out.println("Select the number of the word you were searching for or enter -1 if none of these:");
            
            int selectedIndex = scanner.nextInt();
            scanner.nextLine();  // consume the newline
            
            if (selectedIndex >= 0 && selectedIndex < suggestions.size()) {
                String selectedWord = suggestions.get(selectedIndex);
                updateUserHistory("C:\\Users\\yashs\\OneDrive\\Desktop\\userhistory.txt", selectedWord);  // Add only selected word
                System.out.println("Added '" + selectedWord + "' to your history.");
            } else {
                System.out.println("Please type the exact word you were searching for:");
                String exactWord = scanner.nextLine();
                trieDS.insertWord(exactWord);
                updateUserHistory("C:\\Users\\yashs\\OneDrive\\Desktop\\userhistory.txt", exactWord);
                System.out.println("Added '" + exactWord + "' to your history and Trie.");
            }
        } else {
            System.out.println("No suggestions found. Please type the exact word you were searching for:");
            String exactWord = scanner.nextLine();
            trieDS.insertWord(exactWord);
            updateUserHistory("C:\\Users\\yashs\\OneDrive\\Desktop\\userhistory.txt", exactWord);
            System.out.println("Added '" + exactWord + "' to your history and Trie.");
        }
    }

    // Method to load words from a file and insert them into the Trie
    public static void loadWordsFromFile(Trie trie, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String word;
        while ((word = reader.readLine()) != null) {
            trie.insertWord(word.trim());
        }
        reader.close();
    }
    
    // Method to load user history from a file with frequencies
    public static Map<String, Integer> loadUserHistoryWithFrequency(String historyFile) throws IOException {
        Map<String, Integer> history = new HashMap<>();
        File file = new File(historyFile);
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(historyFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                history.put(word, history.getOrDefault(word, 0) + 1); // Count the frequency
            }
            reader.close();
        }
        return history;
    }
    
    // Method to update user history file with the latest search
    public static void updateUserHistory(String historyFile, String newHistory) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile, true));
        writer.write(newHistory);
        writer.newLine();
        writer.close();
    }
    
    // Method to prioritize or customize suggestions based on user history and frequency
    public static List<String> customizeSuggestions(List<String> suggestions, Map<String, Integer> history) {
        List<String> customizedSuggestions = new ArrayList<>();
        
        // Create a sorted list based on frequency (higher frequency first)
        List<Map.Entry<String, Integer>> sortedHistory = new ArrayList<>(history.entrySet());
        sortedHistory.sort((a, b) -> b.getValue() - a.getValue()); // Sort by frequency in descending order
        
        // Add the most frequent words first, if they exist in the suggestions
        for (Map.Entry<String, Integer> entry : sortedHistory) {
            if (suggestions.contains(entry.getKey())) {
                customizedSuggestions.add(entry.getKey());
            }
        }
        
        // Add remaining suggestions (that are not in history)
        for (String suggestion : suggestions) {
            if (!history.containsKey(suggestion)) {
                customizedSuggestions.add(suggestion);
            }
        }
        
        return customizedSuggestions;
    }
}

class TrieNode {
    Map<Character, TrieNode> children;
    boolean endOfWord;

    TrieNode() {
        children = new HashMap<>();
        endOfWord = false;
    }
}

class Trie {
    
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insertWord(String s) {
        TrieNode current = root;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                node = new TrieNode();
                current.children.put(ch, node);
            }
            current = node;
        }
        current.endOfWord = true;
    }
    
    public List<String> advanceSearch(String prefix){
        List<String> autoCompWords = new ArrayList<>();
        
        TrieNode currentNode = root;
        
        for (int i = 0; i < prefix.length(); i++) {
            currentNode = currentNode.children.get(prefix.charAt(i));
            if (currentNode == null) return autoCompWords;   
        }
        
        searchWords(currentNode, autoCompWords, prefix);
        return autoCompWords;
    }

    private void searchWords(TrieNode currentNode, List<String> autoCompWords, String word) {
        
        if (currentNode == null) return;
        
        if (currentNode.endOfWord) {
            autoCompWords.add(word);
        }
        
        Map<Character, TrieNode> map = currentNode.children;
        for (Character c : map.keySet()) {
            searchWords(map.get(c), autoCompWords, word + String.valueOf(c));
        }
    }
}
