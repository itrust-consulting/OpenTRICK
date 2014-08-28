Private CurrencyManDay As String
Private CurrencyKiloEuroPerYear As String

Private Sub RemoveBrokenReference()
    Dim count As Integer
    count = 1
    While count <= ThisDocument.VBProject.References.count
        If ThisDocument.VBProject.References.Item(count).IsBroken Then
            ThisDocument.VBProject.References.Remove (ThisDocument.VBProject.References.Item(count))
        Else
            count = count + 1
        End If
    Wend
End Sub

Private Function ReferenceExist(name As String) As Boolean
    ReferenceExist = False
    For Each referencee In ThisDocument.VBProject.References
        If referencee.name = name Then
            ReferenceExist = True
            Exit Function
        End If
    Next referencee
End Function
Private Sub AddReference()
    On Error GoTo DisplayError
    'Excel
    
    Call RemoveBrokenReference
    
    If Not ReferenceExist("Excel") Then
        ThisDocument.VBProject.References.AddFromGuid "{00020813-0000-0000-C000-000000000046}", 0, 0
    End If
    On Error GoTo UnknownError
        CurrencyManDay = "\m\d"
        CurrencyKiloEuroPerYear = "k€\/\y"
        Call UpdateChart.UpdateGraphics
    Exit Sub
UnknownError:
    MsgBox "An unknow error occurred while update charts"
    Exit Sub
DisplayError:
    MsgBox "Please enable 'File>Options>Trust Center>Trust Center Settings>Macro Settings>Trust access to the VBA project object model'"
End Sub
Private Sub Document_Open()
    Call AddReference
End Sub
