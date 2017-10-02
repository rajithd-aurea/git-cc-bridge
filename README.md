# Use cases
1. Checkout a file
```
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc test.txt \" rdelantha"
```

2. Undo checkout a file
```
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool uncheckout -rm test.txt \" rdelantha"
```

3. Checkin a file with comments
```
Checkout the file
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc test.txt \" rdelantha"
```
```
Update test.txt content - should not be same as previous
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && echo 'automate' > test.txt \" rdelantha"
```
```
Checkin the file
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool ci -c 'Automate Comment' test.txt \" rdelantha"
```

4. Create a new file
```
Checkout the dir
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool checkout -reserved -nc . \" rdelantha"
```
```
Create new file , add content and checkin
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool mkelem -c 'new file' auto1.txt && echo 'automate' > auto1.txt && /usr/atria/bin/cleartool ci -c 'Automate comment' auto1.txt  \" rdelantha"
```
```
Checkin dir
"/usr/atria/bin/cleartool setview -exec \"cd /vobs/blr/test && /usr/atria/bin/cleartool ci -c 'Automate Comment' . \" rdelantha"
```

5. Create set of new files with directory
6. Create a branch
7. Create a label 
8. Create a label and attach that to set of files
9. Mastership change
