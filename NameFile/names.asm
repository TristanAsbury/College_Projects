; AUTHOR: TRISTAN ASBURY
; STRESS: HIGH
; FEELING AFTER GETTING IT DONE: EUPHORIC
; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT
myData SEGMENT

    buff DB 64 DUP (0)
    currBuffOffset DW $
    numBytesRead DW 0

    commandTail DB 128 DUP (?)
    eoct DW ?
    
    minNumber DW 0  ; WORKING
    currentNumber DW 0

    currentName DB 20 DUP (0), 0Dh, 0Ah
    currentNameLen DW 0

    fileName DB 128 DUP (0) ; input file name
    outputFileName DB 'output.txt', 0

    errorMessage DB 'There was an error opening the file.', '$'
    endOfLineMessage DB 'End of file.', '$'

    inFileHandle DW 0
    outFileHandle DW 0

    eof DB 0

myData ENDS

; CODE SEGMENT

myCode SEGMENT
    assume ds: myData, cs: myCode

;==========MAIN PROC======================
main PROC

    mov ax, myData      ; Make DS point to data segment
	mov ds, ax

    call copyCommandTail    ; get the command tail
    call getMinNumber          ; get the minimum number
    call getFileName        ; gets the input file name
    call openFiles          ; opens input and output file

    mov ax, 0b800h          ; move screen mem add into ax
    mov es, ax              ; move add into es

topMainLoop:
    call getNextName        ; get the name
    call getNextNumber      ; get the number
    mov ax, currentNumber   ; mov current num into ax
    cmp ax, minNumber       ; cmp the current number to min number
    jg callPrintName        ; if the current number is greater than the min number, then write it to file
    cmp eof, 1              ; make sure we're not at end of file
    je exit                 ; if we are, end the program
    jmp topMainLoop         ; go back to top

callPrintName:
    call writeName          ; write the name
    jmp topMainLoop         ; go back to top

exit:
    call endProgram         ; end program
main ENDP
;=========================================

;=========================================
writeName PROC
    push ax bx cx dx

    mov ah, 40h
    mov bx, outFileHandle
    mov cx, currentnameLen
    lea dx, currentName
    int 21h
    
    pop dx cx bx ax
    ret
writeName ENDP
;=========================================

;=========================================
getNextName PROC
    push di ax

    mov currentNameLen, 0   ; make sure the currentNameLen is 0
    call skipWhiteSpace     ; skip white spaces
    lea di, currentName     ; get first character of name

topGetNameLoop:
    call getNextByte        ; get the next byte
    cmp al, ' '             ; is it a space?
    jle exitGetName         ; stop
    inc currentNameLen
    mov ds:[di], al         ; else, add that character to name
    inc di                  ; go to next character
    jmp topGetNameLoop      ; go back to top

exitGetName:
    mov ds:[di], 0Dh        ; add CR LF
    mov ds:[di+1], 0Ah
    add currentNameLen, 2   ; accomodate
    pop ax di
    ret
getNextName ENDP
;=========================================

;=========================================
skipWhiteSpace PROC
    push ax

topSkipWhiteSpace:
    call getNextByte        ; get the next byte
    cmp eof, 1              ; are we at the end of file?
    je whiteSpaceEOF        ; if so, then end the program
    cmp al, ' '             ; is it a whitespace?
    jle topSkipWhiteSpace   ; if so, then skip it

exitSkipWhiteSpace:
    dec currBuffOffset      ; if not, then go back one in the buffer to have an available character
    pop ax
    ret

whiteSpaceEOF:
    call endProgram
skipWhiteSpace ENDP
;=========================================

;=========================================
; ON EXIT: ax = the next byte
getNextByte PROC
    push bx cx si 
    mov si, currBuffOffset  ; put the buff offset address into si
    lea dx, buff            ; dx contains address of buffer
    add dx, numBytesRead    ; add the numBytesRead to dx
    cmp si, dx              ; is the currBuffOffset < the last buff byte pos?
    jl byteAvailable        ; if so, there is a byte available

loadBuff:
    mov bx, inFileHandle    ; else, we must get more
    mov cx, 64              ; make cx 64 (num bytes we're reading)
    lea dx, buff            ; load the address of buffer into dx
    mov ah, 3fh             ; call interrupt to load bytes
    int 21h                 
    jnc goodLoadBytes       ; if the clear flag is not set, then jump to loadBytes
    call displayError       ; 

goodLoadBytes:
    mov numBytesRead, ax    ; mov the number of bytes read to ax
    cmp ax, 0               ; cmp the num of bytes we read
    je eofStatus            ; if we read no bytes, we're at the end of the file
    lea dx, buff            ; load the address of buffer into dx           
    mov currBuffOffset, dx  ; move the currBuffOffset into dx
    jmp byteAvailable       ; goto byteAvailable

eofStatus:
    mov eof, 1              ; we are at end of file
    jmp endGetNextByte      ; get outta here

byteAvailable:
    mov si, currBuffOffset  ; make si point to currentBuffOffset
    mov al, ds:[si]         ; get the byte there
    mov ah, 0               ; clear ah
    inc currBuffOffset      ; go to next byte position

endGetNextByte:
    pop si cx bx
    ret
getNextByte ENDP
;=========================================

;=========================================
openFiles PROC
    push ax dx cx           

    mov ah, 3Dh             ; open input file
    lea dx, fileName
    mov al, 0
    int 21h
    mov inFileHandle, ax
    jc fileError

    mov ah, 3Ch             ; create output file
    lea dx, outputFileName
    mov cl, 00000000b
    int 21h
    mov outFileHandle, ax
    jnc exitOpenFile

fileError:
    call displayError       ; if there was an error at all, then call this

exitOpenFile:
    pop cx dx ax
    ret
openFiles ENDP
;=========================================

;=========================================
copyCommandTail PROC
    push ax cx si di
    
    mov cl, es:[80h]    ; cl contains the number of chars in the tail
    mov ch, 0
    dec cx

    mov si, 81h         ; si contains the actual address of the first character of command tail
    inc si              ; skip the first space

    lea di, commandTail ; di will contain the address of commandTail
    mov eoct, di        ; moves the address of the first char of commandTail to eoct
    add eoct, cx        ; adds the amount of chars to eoct

topCmdTailLoop:
    cmp cx, 0           ; are we at the end?
    je belowCmdTail     ; if so, exit
    mov al, es:[si]     ; mov into al, the character at es:[si]
    mov ds:[di], al     ; mov the char into commandTail var
    inc si              ; move to next char pos
    inc di              ; move to next char pos
    dec cx              ; dec cx
    jmp topCmdTailLoop  ; go back to top

belowCmdTail:
    mov ds:[di], byte ptr 0

    pop di si cx ax
    ret
copyCommandTail ENDP
;=========================================

;=========================================
getFileName PROC 
    push ax si di   

    lea si, commandTail ; load address of first char of commandtail
    lea di, fileName    ; load address of first char of fileName

topGetFileName:
    mov al, ds:[si]     ; mov the char from command tail into al
    cmp al, ' '         ; is it a whitespace
    jle exitGetFileName ; if so, then exit this proc
    mov ds:[di], al     ; mov that char into the fileName
    inc si              ; inc to next char of command tail
    inc di              ; inc to next char of fileName
    jmp topGetFileName  ; repeat

exitGetFileName:
    pop di si ax
    ret
getFileName ENDP 
;=========================================

;=========================================
getMinNumber PROC
    push ax bx cx dx di
    mov di, eoct        ; move the address of the last character in the command tail to di
    dec di              ; go back one to get the one's place character
    mov bx, 1           ; mov bx 1
    mov dx, 0

topGetNumLoop:
    mov al, ds:[di]     ; get character
    cmp al, ' '         ; is it a space
    jle endGetNum       ; if so, we are at the end of the number

    mov ah, 0           ; clear ah
    sub ax, 48          ; get the actual number
    mul bx              ; multiply by power of 10
    add minNumber, ax

    mov ax, bx          ; ax = 1
    mov bx, 10          ; bx = 10
    mul bx              ; 1 * 10 = 10
    mov bx, ax          ; bx = 10
    
    dec di
    jmp topGetNumLoop

endGetNum:
    pop di dx cx bx ax
    ret
getMinNumber ENDP
;=========================================

;=========================================
getNextNumber PROC
    push ax bx cx dx

    call skipWhiteSpace     ; skip white spaces until we get numbers
    mov currentNumber, 0

topGetNumberLoop:
    call getNextByte    ; get the next byte
    cmp ax, ' '         ; is it white space
    jle exitGetNumber   ; if so, then we are done getting the number

    mov bx, ax          ; mov the number into bx
    sub bx, '0'         ; get the actual numeral value
    mov cx, 10          ; mov 10 into cl
    mov ax, currentNumber   ; put the current number int ax
    mul cx              ; multiply the current number by 10
    mov dx, 0
    mov currentNumber, ax   ; put result into currentNumber
    add currentNumber, bx
    jmp topGetNumberLoop

exitGetNumber:
    pop dx cx bx ax
    ret
getNextNumber ENDP
;=========================================

;=========================================
displayError PROC
    lea dx, errorMessage
    mov ah, 09h
    int 21h

    mov ah, 4ch
    int 21h
displayError ENDP
;=========================================

;=========================================
endProgram PROC
    lea dx, endOfLineMessage
    mov ah, 09h
    int 21h

    mov ah, 3eh
    mov bx, inFileHandle
    int 21h

    mov ah, 3eh
    mov bx, outFileHandle
    int 21h 

    mov ah, 4ch
    int 21h
endProgram ENDP
;=========================================

myCode ENDS

END main 
