# 📋 Placement Tracker — Java DSA Project

## What it does
A **console-based Student Placement Management System** that tracks which
students are placed, which companies they applied to, and placement statistics —
built entirely with core Java data structures.

## Data Structures Used
| Structure | Where | Why |
|-----------|-------|-----|
| `HashMap<String, Student>` | Main student database | O(1) lookup by roll number |
| `ArrayList<String>` | Companies each student applied to | Dynamic ordered list |
| `Stack<String>` | Action history | O(1) undo with LIFO behaviour |

## How to Run
```bash
# 1. Compile
javac PlacementTracker.java

# 2. Run
java PlacementTracker
```
> Requires JDK 17+ (uses switch expression syntax `->`)

## Features

## Features

- Add new student records
- Search students using roll number
- Track company applications
- Mark students as placed
- Generate placement statistics
- Undo recent actions using Stack