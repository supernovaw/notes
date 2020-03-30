# notes.bin
notes.bin is the internal storage file used by supernovaw.notes.Notes class. It contains the following information about each note:
* title
* contents
* time of creation
* time of last modification
* time of last access
* whether it has a deadline
* deadline (if exists)

### File structure:
* 4 bytes (int): the number of notes total
* Each note in succession (no data other than the following)

### Each note:
| Bytes      | Description                            |
|------------|----------------------------------------|
| 8, long    | time of creation                       |
| 8, long    | time of last modification              |
| 8, long    | time of last access                    |
| 1          | indicator of whether is has a deadline |
| 8 or 0     | (optional) deadline time               |
| 4, integer | number of title string bytes           |
| optional   | title string UTF-8 bytes               |
| 4, integer | number of note text string bytes       |
| optional   | note text string UTF-8 bytes           |

Deadline indicator byte can be
* 0x00 — no deadline
* 0x01 — has deadline