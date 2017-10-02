# Use cases

```
Common Command pattern
"/usr/atria/bin/cleartool setview -exec \" $ \" <view_name>"
```

1. Checkout a file
```
$=cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc test.txt
```

2. Undo checkout a file
```
$=cd /vobs/blr/test && /usr/atria/bin/cleartool uncheckout -rm test.txt
```

3. Checkin a file with comments
```
$=cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc test.txt && echo 'automate' > test.txt && /usr/atria/bin/cleartool ci -c 'Automate Comment' test.txt
```

4. Create a new file
```
$=cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc . && /usr/atria/bin/cleartool mkelem -c 'new file' auto1.txt && echo 'automate' > auto1.txt && /usr/atria/bin/cleartool ci -c 'Automate comment' auto1.txt && /usr/atria/bin/cleartool ci -c 'Automate Comment' .
```

5. Create set of new files
6. Create a new directory
7. Remove files
8. Remove directory
9. Create a branch
```
$=cd /vobs/blr/test && /usr/atria/bin/cleartool mkbrtype <branch name> && /usr/atria/bin/cleartool mkbranch <branch name> <file_with_version@@/main/181>
```

10. Create a label
11. Create a label and attach that to set of files
12. Mastership change

