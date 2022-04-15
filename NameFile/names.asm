; AUTHOR: TRISTAN ASBURY
; STRESS: HIGH
; FEELING AFTER GETTING IT DONE: EUPHORIC
; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT
myData SEGMENT

    buff DB 64 DUP (?)
    currBuffOffset DW $
    numBytesRead DW ?

    commandTail DB 128 DUP (?)
    eoct DW ?

    fileName DB 128 DUP (0) ; file name
    minNumber DW 0  ; WORKING

    inFileHandle DW ?

myData ENDS

; CODE SEGMENT

myCode SEGMENT
    assume ds: myData, cs: myCode

;==========MAIN PROC======================
main PROC

    mov ax, myData      ; Make DS point to data segment
	mov ds, ax

    call copyCommandTail
    call getNumber

    mov ax, 0b800h;
    mov es, ax

    mov si, minNumber
    mov di, 320

topLoop:
    cmp si, 0
    je exit
    mov es:[di], byte ptr 'a'
    mov es:[di+1], byte ptr 00001111b
    add di, 2
    dec si
    jmp topLoop

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt to give control back to OS
    int 21h
main ENDP
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
    push

    lea si, commandTail
    lea di,  

    pop
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
