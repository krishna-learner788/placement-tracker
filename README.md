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
- Add / search / view students
- Mark as placed at a company
- Record company applications per student
- View placement statistics (placed %, average CGPA)
- **Undo** last action (Stack-based)

## Resume Line
> *"Developed a Java console application for student placement tracking using
> HashMap for O(1) lookups, ArrayList for application management, and a
> Stack-based undo feature — demonstrating core DSA concepts."*

## GitHub Steps
```bash
git init
git add PlacementTracker.java README.md
git commit -m "feat: add placement tracker with HashMap + ArrayList + Stack"
git remote add origin https://github.com/<your-username>/placement-tracker.git
git push -u origin main
```
