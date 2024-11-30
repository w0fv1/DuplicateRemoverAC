# Aho-Corasick 文本搜索算法

## 什么是 Aho-Corasick 算法？

Aho-Corasick 算法是一种高效的多模式字符串匹配算法，由 Alfred V. Aho 和 Margaret J. Corasick 于 1975 年提出。该算法能够在给定的文本中同时搜索多个模式串，广泛应用于文本编辑器、网络安全、数据挖掘等领域。与传统的单模式匹配算法相比，Aho-Corasick 算法在处理多个模式串时具有更高的效率，特别适合于需要同时检测多个关键词的场景。

## 为什么选择 Aho-Corasick 算法？

选择 Aho-Corasick 算法的主要原因包括：

1. **高效性**：Aho-Corasick 算法的时间复杂度为 O(N + M + Z)，其中 N 是文本长度，M 是所有模式串的总长度，Z 是匹配结果的总数量。这使得它在处理大规模文本和大量模式串时表现出色。

2. **一次遍历**：该算法仅需对文本进行一次遍历，避免了多次扫描文本的开销。

3. **适应性强**：适用于多种应用场景，如病毒扫描、敏感词过滤、日志分析等。

4. **自动机结构**：通过构建有限状态自动机，算法能够高效地处理复杂的匹配需求。

## Aho-Corasick 算法的核心概念

要理解 Aho-Corasick 算法，我们需要掌握以下几个核心概念：

### 1. Trie（字典树）

Trie 是一种树形数据结构，用于高效地存储和检索字符串集合。每个节点代表一个字符，路径代表一个字符串。Trie 的优势在于共享前缀，节省空间和时间。

**示例**：

假设有以下模式串：

- "he"
- "she"
- "his"
- "hers"

对应的 Trie 如下：

```
        root
       /    \
      h      s
      |      |
      e      h
     / \      \
    r   i      e
    |   |      |
    s   s      r
                |
                s
```

### 2. 失败指针（Fail Pointers）

在 Trie 中，当无法继续匹配时，失败指针指向下一个可能匹配的位置。失败指针的引入，使得算法能够在不回溯文本指针的情况下，继续匹配。当当前字符无法匹配时，算法会根据失败指针跳转到另一个节点继续尝试匹配，从而提高了匹配效率。

### 3. 输出列表（Output List）

每个节点维护一个输出列表，记录以该节点结尾的所有模式串。当匹配到该节点时，输出列表中的模式串即为当前匹配的结果。这样，算法不仅能够检测到模式串的存在，还能识别出具体匹配了哪些模式串。

## Aho-Corasick 算法的工作原理

AC 算法主要分为两个阶段：

### 阶段一：构建 Trie 和失败指针

1. **构建 Trie**：将所有模式串插入到 Trie 中。每个模式串对应 Trie 中的一条路径，节点之间共享公共前缀。

2. **构建失败指针**：使用广度优先搜索（BFS）遍历 Trie，为每个节点设置失败指针。失败指针指向当前节点的最长后缀节点，即在当前路径无法继续匹配时，应该跳转到哪个节点继续匹配。通过这种方式，算法能够在遇到不匹配时快速跳转，避免重复扫描已匹配的部分。

### 阶段二：在文本中进行匹配

1. **初始化**：从 Trie 的根节点开始，逐字符遍历文本。

2. **匹配过程**：
    - 对于每个字符，尝试在当前节点的子节点中查找匹配。
    - 如果匹配成功，移动到相应子节点，继续匹配下一个字符。
    - 如果匹配失败，则跟随失败指针继续查找，直到找到匹配或回到根节点。

3. **记录匹配结果**：每当到达一个节点，检查其输出列表，将匹配结果记录下来。这样，算法能够识别出所有在文本中出现的模式串。

**优势**：整个匹配过程只需遍历文本一次，时间复杂度为 O(N + M + Z)，其中 N 是文本长度，M 是所有模式串的总长度，Z 是匹配结果的总数量。这使得 Aho-Corasick 算法在处理大规模数据时表现优异。

## 通过示例代码理解 Aho-Corasick 算法

### 代码功能概述

本项目包含两个主要类：

1. **ACDuplicateRemover**：用于在两个句子之间删除重复的子串。通过提取第一个句子的所有子串，构建 Aho-Corasick 自动机，然后在第二个句子中搜索这些子串并删除重复部分。

2. **ACMultiDuplicateRemover**：扩展了单句子的重复删除功能，支持多个句子的重复检测和删除。它识别在多个句子中重复出现的子串，并在非首个出现的句子中删除这些重复部分。

### 关键代码部分解析

#### 1. 构建 Trie 和失败指针

在 `AhoCorasickAutomaton` 类中，`addPattern` 方法用于将模式串插入到 Trie 中。`buildFailPointers` 方法使用 BFS 遍历 Trie，构建每个节点的失败指针。

```java
public void addPattern(String pattern) {
    Node current = root;
    for (char ch : pattern.toCharArray()) {
        current = current.children.computeIfAbsent(ch, c -> new Node());
    }
    current.output.add(pattern);
}

public void buildFailPointers() {
    Queue<Node> queue = new LinkedList<>();
    for (Node child : root.children.values()) {
        child.fail = root;
        queue.offer(child);
    }

    while (!queue.isEmpty()) {
        Node current = queue.poll();
        for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
            char ch = entry.getKey();
            Node child = entry.getValue();

            Node failNode = current.fail;
            while (failNode != null && !failNode.children.containsKey(ch)) {
                failNode = failNode.fail;
            }
            child.fail = (failNode != null) ? failNode.children.get(ch) : root;
            child.output.addAll(child.fail.output);
            queue.offer(child);
        }
    }
}
```

#### 2. 在文本中进行匹配

`search` 方法用于在给定的文本中查找所有匹配的模式串。它利用已经构建好的 Trie 和失败指针，高效地完成多模式匹配。

```java
public List<MatchResult> search(String text) {
    List<MatchResult> results = new ArrayList<>();
    Node current = root;
    for (int i = 0; i < text.length(); i++) {
        char ch = text.charAt(i);
        while (current != root && !current.children.containsKey(ch)) {
            current = current.fail;
        }
        if (current.children.containsKey(ch)) {
            current = current.children.get(ch);
        }

        for (String pattern : current.output) {
            int start = i - pattern.length() + 1;
            int end = i + 1;
            results.add(new MatchResult(start, end, pattern));
        }
    }
    return results;
}
```

#### 3. 删除重复子串

在 `ACDuplicateRemover` 和 `ACMultiDuplicateRemover` 类中，`removeDuplicates` 方法使用 Aho-Corasick 自动机查找重复子串，并标记需要删除的位置。最终，通过遍历标记数组，构建删除后的字符串。

```java
public static String removeDuplicates(String sentence1, String sentence2, int minSubstringLength) {
    if (sentence1 == null || sentence2 == null || sentence2.isEmpty()) {
        return sentence2;
    }

    List<String> patterns = extractSubstrings(sentence1, minSubstringLength, 100);
    if (patterns.isEmpty()) {
        return sentence2;
    }

    AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
    for (String pattern : patterns) {
        automaton.addPattern(pattern);
    }
    automaton.buildFailPointers();

    List<MatchResult> matches = automaton.search(sentence2);
    if (matches.isEmpty()) {
        return sentence2;
    }

    boolean[] toDelete = new boolean[sentence2.length()];
    for (MatchResult match : matches) {
        for (int i = match.start; i < match.end; i++) {
            toDelete[i] = true;
        }
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < sentence2.length(); i++) {
        if (!toDelete[i]) {
            sb.append(sentence2.charAt(i));
        }
    }

    String result = sb.toString().replaceAll("\\s{2,}", " ").trim();
    return result;
}
```

#### 4. 处理多个句子的重复删除

`ACMultiDuplicateRemover` 类扩展了单句子的处理逻辑，通过识别多个句子中重复出现的子串，并在非首个出现的句子中删除这些子串。

```java
public static List<String> removeDuplicates(List<String> sentences, int minSubstringLength) {
    if (sentences == null || sentences.isEmpty()) {
        return sentences;
    }

    // 提取所有句子的子串，并构建模式串到句子索引的映射
    Map<String, Set<Integer>> patternToSentenceIndices = new HashMap<>();
    List<Set<String>> sentenceSubstrings = new ArrayList<>();

    for (int idx = 0; idx < sentences.size(); idx++) {
        String sentence = sentences.get(idx);
        Set<String> substrings = extractSubstrings(sentence, minSubstringLength, 100);
        sentenceSubstrings.add(substrings);
        for (String substr : substrings) {
            patternToSentenceIndices.computeIfAbsent(substr, k -> new HashSet<>()).add(idx);
        }
    }

    // 识别出现在多个句子中的重复模式串
    Set<String> duplicatePatterns = new HashSet<>();
    for (Map.Entry<String, Set<Integer>> entry : patternToSentenceIndices.entrySet()) {
        if (entry.getValue().size() > 1) {
            duplicatePatterns.add(entry.getKey());
        }
    }

    if (duplicatePatterns.isEmpty()) {
        return sentences;
    }

    // 构建只包含重复模式串的 Aho-Corasick 自动机
    AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
    for (String pattern : duplicatePatterns) {
        automaton.addPattern(pattern);
    }
    automaton.buildFailPointers();

    // 标记每个句子中需要删除的字符位置
    List<boolean[]> toDeleteList = new ArrayList<>();
    for (String sentence : sentences) {
        if (sentence == null || sentence.isEmpty()) {
            toDeleteList.add(null);
        } else {
            toDeleteList.add(new boolean[sentence.length()]);
        }
    }

    // 遍历每个句子，查找并标记需要删除的位置
    for (int currentIdx = 0; currentIdx < sentences.size(); currentIdx++) {
        String sentence = sentences.get(currentIdx);
        if (sentence == null || sentence.isEmpty()) {
            continue;
        }

        List<AhoCorasickAutomaton.MatchResult> matches = automaton.search(sentence);
        boolean[] toDelete = toDeleteList.get(currentIdx);
        if (toDelete == null) {
            continue;
        }

        for (AhoCorasickAutomaton.MatchResult match : matches) {
            Set<Integer> sourceIndices = patternToSentenceIndices.get(match.pattern);
            if (sourceIndices == null) continue;

            // 找到模式串的最低优先级句子（即第一个出现的句子）
            int minIndex = Collections.min(sourceIndices);

            if (currentIdx != minIndex) {
                // 仅在当前句子不是最优先级句子时，标记删除
                for (int pos = match.start; pos < match.end; pos++) {
                    if (pos >= 0 && pos < toDelete.length) {
                        toDelete[pos] = true;
                    }
                }
            }
        }
    }

    // 构建删除后的句子（保留符号和空格）
    List<String> resultSentences = new ArrayList<>();
    for (int i = 0; i < sentences.size(); i++) {
        String sentence = sentences.get(i);
        if (sentence == null || sentence.isEmpty()) {
            resultSentences.add(sentence);
            continue;
        }
        boolean[] toDelete = toDeleteList.get(i);
        if (toDelete == null) {
            resultSentences.add(sentence);
            continue;
        }
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < sentence.length(); j++) {
            if (!toDelete[j]) {
                sb.append(sentence.charAt(j));
            }
        }
        // 清理多余的空格
        String cleaned = sb.toString().replaceAll("\\s{2,}", " ").trim();
        resultSentences.add(cleaned);
    }

    return resultSentences;
}
```


## 总结

Aho-Corasick 算法通过构建高效的 Trie 结构和失败指针，实现了多模式串的高效匹配。在本项目中，我们利用该算法实现了在文本中检测和删除重复子串的功能，支持单句子和多句子的处理。通过丰富的测试用例，我们验证了算法在各种场景下的性能和准确性。Aho-Corasick 算法的高效性和灵活性使其在文本处理、数据挖掘等领域具有广泛的应用前景。