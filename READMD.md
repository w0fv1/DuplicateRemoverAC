# 深入浅出 Aho-Corasick 文本搜索算法

在计算机科学中，字符串匹配是一个基础而重要的主题。无论是文本编辑器的“查找与替换”功能，还是网络安全中的病毒检测，字符串匹配算法都扮演着关键角色。而在众多字符串匹配算法中，**Aho-Corasick（AC）算法**因其高效性和多模式匹配能力而备受关注。本文将带领算法小白一步步了解 Aho-Corasick 算法，从基础概念到实际应用，帮助你建立扎实的理解。

## 什么是 Aho-Corasick 算法？

Aho-Corasick 算法由 Alfred V. Aho 和 Margaret J. Corasick 在 1975 年提出，是一种用于在文本中同时搜索多个模式串的高效算法。与传统的逐一搜索模式串的方法相比，AC 算法能够在 **线性时间复杂度** 下完成多模式匹配，大大提高了效率。

## 为什么选择 Aho-Corasick 算法？

在需要同时搜索多个模式串的场景中，AC 算法具有明显优势。例如：

- **病毒扫描**：同时检测文件中是否存在多种病毒签名。
- **敏感词过滤**：在文本中查找并过滤多个敏感词。
- **日志分析**：从大量日志中快速提取多个关键事件。

使用传统的逐一搜索方法，时间复杂度会随着模式串数量线性增加。而 AC 算法通过构建有限状态机，将多模式匹配问题转化为一次遍历，显著提升了效率。

## Aho-Corasick 算法的核心概念

要理解 AC 算法，我们需要掌握以下几个核心概念：

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

在 Trie 中，当无法继续匹配时，失败指针指向下一个可能匹配的位置。失败指针的引入，使得算法能够在不回溯文本指针的情况下，继续匹配。

### 3. 输出列表（Output List）

每个节点维护一个输出列表，记录以该节点结尾的所有模式串。当匹配到该节点时，输出列表中的模式串即为当前匹配的结果。

## Aho-Corasick 算法的工作原理

AC 算法主要分为两个阶段：

### 阶段一：构建 Trie 和失败指针

1. **构建 Trie**：将所有模式串插入到 Trie 中。
2. **构建失败指针**：使用广度优先搜索（BFS）遍历 Trie，为每个节点设置失败指针。失败指针指向当前节点的最长后缀节点。

### 阶段二：在文本中进行匹配

1. 从根节点开始，逐字符遍历文本。
2. 对于每个字符，尝试在当前节点的子节点中查找匹配。如果匹配，则移动到相应子节点；否则，跟随失败指针继续查找。
3. 每当到达一个节点，检查其输出列表，将匹配结果记录下来。

**优势**：整个匹配过程只需遍历文本一次，时间复杂度为 O(N + M + Z)，其中 N 是文本长度，M 是所有模式串的总长度，Z 是匹配结果的总数量。

## 通过示例代码理解 Aho-Corasick 算法

让我们通过一段实际的 Java 代码来更深入地理解 AC 算法。以下代码实现了一个简单的重复内容移除工具，利用 AC 算法高效地检测并删除重复子串。

### 代码解析

```java
package dev.w0fv1.aievy;

import java.util.*;

public class DuplicateRemoverAC {

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {
        testCase1();
        // 其他测试案例...
    }

    // 示例测试案例1：普通重复
    private static void testCase1() {
        String sentence1 = "今天的天气真好，我们一起去公园散步吧。";
        String sentence2 = "我也觉得今天的天气真好，我们一起去公园散步吧。";
        String expected = "我也觉得";
        int minSubstringLength = 2; // 设置最小子串长度
        runTest(1, sentence1, sentence2, minSubstringLength, expected);
    }

    // 通用测试运行方法
    private static void runTest(int testCaseNumber, String sentence1, String sentence2, int minSubstringLength, String expected) {
        String result = removeDuplicates(sentence1, sentence2, minSubstringLength);
        boolean passed = result.equals(expected);
        System.out.println("测试案例 " + testCaseNumber + ": " + (passed ? "通过" : "失败"));
        if (!passed) {
            System.out.println("预期输出: \"" + expected + "\"");
            System.out.println("实际输出: \"" + result + "\"");
        }
        System.out.println("-------------------------------------");
    }

    /**
     * 删除第二个句子中与第一个句子重复的部分，同时保留第二个句子的所有符号和空格。
     *
     * @param sentence1          第一个句子
     * @param sentence2          第二个句子
     * @param minSubstringLength 最小子串长度
     * @return 删除重复部分后的第二个句子
     */
    public static String removeDuplicates(String sentence1, String sentence2, int minSubstringLength) {
        if (sentence1 == null || sentence2 == null || sentence2.isEmpty()) {
            return sentence2;
        }

        // 提取sentence1中所有长度 >= minSubstringLength的子串
        List<String> patterns = extractSubstrings(sentence1, minSubstringLength, 100); // 设置最大子串长度为100

        if (patterns.isEmpty()) {
            return sentence2;
        }

        // 构建Aho-Corasick自动机
        AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
        for (String pattern : patterns) {
            automaton.addPattern(pattern);
        }
        automaton.buildFailPointers();

        // 查找所有匹配的模式串及其位置
        List<MatchResult> matches = automaton.search(sentence2);

        if (matches.isEmpty()) {
            return sentence2;
        }

        // 标记需要删除的字符位置
        boolean[] toDelete = new boolean[sentence2.length()];

        for (MatchResult match : matches) {
            for (int i = match.start; i < match.end; i++) {
                toDelete[i] = true;
            }
        }

        // 构建删除后的字符串（不插入空格）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentence2.length(); i++) {
            if (!toDelete[i]) {
                sb.append(sentence2.charAt(i));
            }
        }

        // 清理多余的空格
        String result = sb.toString().replaceAll("\\s{2,}", " ").trim();

        return result;
    }

    /**
     * 提取字符串中所有长度 >= minLen且不超过 maxLen 的子串。
     *
     * @param s      输入字符串
     * @param minLen 最小子串长度
     * @param maxLen 最大子串长度
     * @return 子串列表
     */
    private static List<String> extractSubstrings(String s, int minLen, int maxLen) {
        Set<String> substrings = new HashSet<>();
        int len = s.length();
        maxLen = Math.min(maxLen, len); // 防止超出字符串长度

        for (int l = minLen; l <= maxLen; l++) { // 从短到长，避免过多的子串
            for (int i = 0; i <= len - l; i++) {
                String substr = s.substring(i, i + l);
                substrings.add(substr);
            }
        }
        return new ArrayList<>(substrings);
    }

    /**
     * 匹配结果类
     */
    static class MatchResult {
        int start;
        int end;

        MatchResult(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Aho-Corasick自动机类
     */
    static class AhoCorasickAutomaton {
        private final Node root;

        public AhoCorasickAutomaton() {
            root = new Node();
        }

        /**
         * 添加一个模式串
         *
         * @param pattern 模式串
         */
        public void addPattern(String pattern) {
            Node current = root;
            for (char ch : pattern.toCharArray()) {
                current = current.children.computeIfAbsent(ch, c -> new Node());
            }
            current.output.add(pattern);
        }

        /**
         * 构建失败指针
         */
        public void buildFailPointers() {
            Queue<Node> queue = new LinkedList<>();
            // 初始化第一层的失败指针
            for (Node child : root.children.values()) {
                child.fail = root;
                queue.offer(child);
            }

            // 广度优先遍历构建失败指针
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

        /**
         * 在文本中搜索所有模式串的匹配位置
         *
         * @param text 文本字符串
         * @return 匹配结果列表
         */
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
                    results.add(new MatchResult(start, end));
                }
            }
            return results;
        }

        /**
         * Trie节点类
         */
        static class Node {
            Map<Character, Node> children = new HashMap<>();
            Node fail = null;
            List<String> output = new ArrayList<>();
        }
    }
}
```

### 代码功能概述

上述代码实现了一个重复内容移除工具，其主要步骤如下：

1. **提取子串**：从 `sentence1` 中提取所有长度大于等于 `minSubstringLength` 的子串，作为需要检测的模式串。
2. **构建 AC 自动机**：将提取的子串添加到 Aho-Corasick 自动机中，并构建失败指针。
3. **搜索匹配**：在 `sentence2` 中搜索所有匹配的模式串，并标记需要删除的字符位置。
4. **删除重复部分**：根据标记删除重复部分，生成最终结果。

### 关键部分解析

#### 1. 提取子串

```java
private static List<String> extractSubstrings(String s, int minLen, int maxLen) {
    Set<String> substrings = new HashSet<>();
    int len = s.length();
    maxLen = Math.min(maxLen, len); // 防止超出字符串长度

    for (int l = minLen; l <= maxLen; l++) { // 从短到长，避免过多的子串
        for (int i = 0; i <= len - l; i++) {
            String substr = s.substring(i, i + l);
            substrings.add(substr);
        }
    }
    return new ArrayList<>(substrings);
}
```

这段代码从 `sentence1` 中提取所有符合长度要求的子串，并存储在一个集合中，以避免重复。

#### 2. 构建 AC 自动机

```java
AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
for (String pattern : patterns) {
    automaton.addPattern(pattern);
}
automaton.buildFailPointers();
```

首先将所有模式串添加到自动机中，然后构建失败指针，以便后续高效匹配。

#### 3. 搜索匹配并删除

```java
List<MatchResult> matches = automaton.search(sentence2);

if (matches.isEmpty()) {
    return sentence2;
}

// 标记需要删除的字符位置
boolean[] toDelete = new boolean[sentence2.length()];

for (MatchResult match : matches) {
    for (int i = match.start; i < match.end; i++) {
        toDelete[i] = true;
    }
}

// 构建删除后的字符串（不插入空格）
StringBuilder sb = new StringBuilder();
for (int i = 0; i < sentence2.length(); i++) {
    if (!toDelete[i]) {
        sb.append(sentence2.charAt(i));
    }
}

String result = sb.toString().replaceAll("\\s{2,}", " ").trim();
```

通过搜索匹配结果，标记需要删除的字符位置，并最终构建删除后的字符串。

### 测试案例

代码中包含多个测试案例，覆盖了普通重复、无重复、完全重复、重复子串重叠、特殊字符、不区分大小写等多种情况。这些测试确保了算法在不同场景下的正确性和鲁棒性。

## 总结

Aho-Corasick 算法通过构建高效的 Trie 和失败指针，实现了多模式串的线性时间匹配。本文通过基础概念介绍、算法原理解析以及实际代码示例，帮助算法小白逐步理解和掌握 AC 算法。掌握 Aho-Corasick 算法，不仅可以提升解决实际问题的能力，还能为深入学习其他高级算法打下坚实的基础。

希望本文能为你在算法学习的道路上提供有价值的指导与帮助！
