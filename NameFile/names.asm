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
    
    currentNumberString DB 8 DUP (0)
    currentNumber DW 0
    currentName DB 22 DUP (0)
    minNumber DW 0  ; WORKING

    fileName DB 128 DUP (0) ; input file name
    outputFileName DB 'output.txt', 0

    errorMessage DB 'There was an error opening the file.', '$'
    endOfLineMessage DB 'End of file.', '$'

    inFileHandle DW 0
    outFileHandle DW 0

    numBytesWritten DB 0

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
    call getNumber          ; get the minimum number
    call getFileName        ; gets the input file name
    call openFiles          ; opens input and output file

    mov ax, 0b800h;
    mov es, ax
    mov di, 320

topMainLoop:
    call getNextName        
    call getNextNumber
    mov ax, minNumber
    cmp currentNumber, ax
    jg printNameCall
    cmp eof, 1

    push ax
    mov ah, 10h
    int 16h
    pop ax

    jne topMainLoop

printNameCall:
    call printName
    jmp topMainLoop

exit:
    mov ah, 4ch     ; These two instructions use a DOS interrupt to give control back to OS
    int 21h
main ENDP
;=========================================

;=========================================
printName PROC
    push ax si di

    lea si, currentName
    mov di, 320

topPrintName:
    mov al, ds:[si]
    cmp al, 0
    je exitPrintName
    mov es:[di], al
    mov es:[di+1], byte ptr 00001111b
    add di, 2
    inc si
    jmp topPrintName

exitPrintName:
    pop di si ax
    ret
printName ENDP
;=========================================

;=========================================
getNextName PROC
    push di ax

    call clearName  ; clear the name
    call skipWhiteSpace ; skip white spaces
    lea di, currentName    ; get first character of name
topGetNameLoop:
    call getNextByte    ; get the next byte
    cmp al, ' ' ; is it a space?
    jle exitGetName ; stop
    mov ds:[di], al ; else, add that character to name
    inc di          ; go to next character
    jmp topGetNameLoop  ; go back to top

exitGetName:
    pop ax di
    ret
getNextName ENDP
;=========================================

;=========================================
clearCurrentNumber PROC
    push cx di
    mov currentNumber, 0
    mov cx, 8

    lea di, currentNumberString

topClearNum:
    cmp cx, 0
    je exitClearNum
    mov ds:[di], byte ptr 0
    dec cx
    inc di
    jmp topClearNum

exitClearNum:
    pop di cx
    ret
clearCurrentNumber ENDP
;=========================================

;=========================================
clearName PROC
    push cx di

    mov cx, 22  ; we want to clear 22 space
    lea di, currentName ; get the address of the first character of name

topClearName:
    cmp cx, 0   ; are we at the end of name
    je exitClearName    ; if so, we are done clearing the name
    mov ds:[di], byte ptr 0  ; replace the character with 0
    inc di  ; go to next character
    dec cx  ; dec cx
    jmp topClearName    ; go back to top

exitClearName:
    pop di cx
    ret
clearName ENDP
;=========================================

;=========================================
getNextNumber PROC
    push ax di 

    call clearCurrentNumber
    call skipWhiteSpace
    lea di, currentNumberString

topGetNumberLoop:
    call getNextByte
    cmp al, ' '
    jle exitGetNumber
    mov ds:[di], al
    inc di
    jmp topGetNumberLoop


exitGetNumber:
    call convertNumber
    pop di ax
    ret
getNextNumber ENDP
;=========================================

;=========================================
writeNameToFile PROC
    ret
writeNameToFile ENDP
;=========================================

;=========================================
skipWhiteSpace PROC
    push ax

topSkipWhiteSpace:
    call getNextByte
    cmp eof, 1
    je whiteSpaceEOF
    cmp al, ' '
    jle topSkipWhiteSpace

exitSkipWhiteSpace:
    dec currBuffOffset
    pop ax
    ret

whiteSpaceEOF:
    call endProgram
skipWhiteSpace ENDP
;=========================================

;=========================================
; ON EXIT: AL = the next byte
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
    lea dx, buff                
    mov currBuffOffset, dx
    jmp byteAvailable

eofStatus:
    mov eof, 1
    jmp endGetNextByte

byteAvailable:
    mov si, currBuffOffset
    mov al, ds:[si]
    inc currBuffOffset

endGetNextByte:
    pop si cx bx
    ret
getNextByte ENDP
;=========================================

;=========================================
openFiles PROC
    push ax dx cx

    ;open input
    mov ah, 3Dh
    lea dx, fileName
    mov al, 0
    int 21h
    mov inFileHandle, ax
    jc fileError

    ;create output
    mov ah, 3Ch
    lea dx, outputFileName
    mov cl, 00000000b
    int 21h
    mov outFileHandle, ax
    jnc exitOpenFile

fileError:
    call displayError

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

    lea si, commandTail
    lea di, fileName

topGetFileName:
    mov al, ds:[si]
    cmp al, ' '
    jle exitGetFileName
    mov ds:[di], al
    inc si
    inc di
    jmp topGetFileName

exitGetFileName:
    pop di si ax
    ret
getFileName ENDP 
;=========================================

;=========================================
getNumber PROC
    push ax bx cx dx di
    mov di, eoct    ; move the address of the last character in the command tail to di
    dec di          ; go back one to get the one's place character
    mov bx, 1
    mov dx, 0

topGetNumLoop:
    mov al, ds:[di] ;get character
    cmp al, ' ' ; is it a space
    jle endGetNum    ; if so, we are at the end of the number

    mov ah, 0   ; clear ah
    sub ax, 48  ; get the actual number
    mul bx      ; multiply by power of 10
    add minNumber, ax

    mov ax, bx  ; ax = 1
    mov bx, 10  ; bx = 10
    mul bx  ; 1 * 10 = 10
    mov bx, ax  ; bx = 10
    
    dec di
    jmp topGetNumLoop

endGetNum:
    pop di dx cx bx ax
    ret
getNumber ENDP
;=========================================

;=========================================
convertNumber PROC
    push ax bx si

    lea si, currentNumberString
    mov bx, 1

topConvertNumLoop:
    mov al, ds:[si]
    cmp al, 0
    je exitConvertNum

    mov ah, 0
    sub ax, 48
    mul bx
    add currentNumber, ax

    mov ax, bx
    mov bx, 10
    mul bx
    mov bx, ax

    inc si
    jmp topConvertNumLoop

exitConvertNum:
    pop si bx ax
    ret
convertNumber ENDP
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
