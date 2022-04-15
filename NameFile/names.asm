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
    numBytesRead DW ?

    commandTail DB 128 DUP (?)
    eoct DW ?
    
    currentNumberString DB 8 DUP (0)
    currentNumber DW 0
    minNumber DW 0  ; WORKING

    fileName DB 128 DUP (0) ; input file name
    outputFileName DB 'output.txt', 0

    errorMessage DB 'There was an error opening the file.', '$'

    fileHandle DW 0

    inFileHandle DW ?

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

topMainLoop:
    call getNextName
    call getNextNumber
    ;compare the number (presumably in ax to the minNumber)
    
    lea si, fileName
    mov di, 320

topLoop:
    mov al, ds:[si]
    cmp al, 0
    je exit
    mov es:[di], al
    mov es:[di+1], byte ptr 00001111b
    add di, 2
    inc si
    jmp topLoop

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt to give control back to OS
    int 21h
main ENDP
;=========================================

;=========================================
openFiles PROC
    push ax dx

    ;open input
    mov ah, 3Dh
    lea dx, fileName
    mov al, 0
    int 21h
    mov fileHandle, ax
    jnc exitOpenFile

    ;create output
    mov ah, 3Ch
    lea dx, outputFileName
    mov cl, 00000000b
    mov filehandle, ax
    jnc exitOpenFile

fileError:
    lea dx, errorMessage
    mov ah, 09h
    int 21h

exitOpenFile:
    pop dx ax
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

myCode ENDS

END main 
