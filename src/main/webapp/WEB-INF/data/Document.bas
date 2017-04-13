
Public CurrencyManDay As String
Public CurrencyKiloEuroPerYear As String

Private Sub RemoveBrokenReference()
    For i = ThisDocument.VBProject.References.count To 1 Step -1
       Set currentReference = ThisDocument.VBProject.References.Item(i)
       If currentReference.isbroken = True Then
           ThisDocument.VBProject.References.Remove currentReference
       End If
    Next i
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

Private Function IsFirstOpening() As Boolean
    On Error GoTo HasError
    If isTemplate Then
        IsFirstOpening = False
        Exit Function
    End If
     IsFirstOpening = Not isTemplate And ActiveDocument.CustomDocumentProperties.Item("FirstOpening").Value
     ActiveDocument.CustomDocumentProperties.Item("FirstOpening").Value = False
     Exit Function
HasError:
    On Error GoTo AddPropertyError
    IsFirstOpening = Not isTemplate
    ActiveDocument.CustomDocumentProperties.Add name:="FirstOpening", LinkToContent:=False, Value:=False, _
    Type:=msoPropertyTypeBoolean
AddPropertyError:
    Debug.Print Err.Description
End Function
Private Sub AddReference()
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
    If IsFirstOpening Then
        Call UpdateMeasureTable
        Call AddReference
    End If
End Sub

Private Sub ReplaceTableFormat(table As table, oldValue As String, newValue As String)
    table.Select
    Selection.Find.ClearFormatting
    With Selection.Find
        .Text = oldValue
        .Replacement.Text = newValue
        .Forward = True
        .Wrap = wdFindStop
        .Format = False
        .MatchCase = False
        .MatchWholeWord = False
        .MatchKashida = False
        .MatchDiacritics = False
        .MatchAlefHamza = False
        .MatchControl = False
        .MatchWildcards = False
        .MatchSoundsLike = False
        .MatchAllWordForms = False
    End With
    Selection.Collapse
End Sub

Private Sub removeSelectionBorder()
   
End Sub

Private Sub formatRiskHeatMap(table As table)
    
    Dim count As Integer
    Dim selectedRange As Range
    
    table.ApplyStyleFirstColumn = True
    table.ApplyStyleLastColumn = True
    table.ApplyStyleRowBands = False
    table.ApplyStyleHeadingRows = False
    count = table.Rows.count
    table.Rows(count).Select
    Selection.Paragraphs.style = "TabHeader1"
    Selection.Paragraphs.Alignment = wdAlignRowCenter
    Selection.Cells.Merge
    
    Set selectedRange = table.Rows(1).Cells(1).Range
    selectedRange.End = table.Rows(count - 1).Cells(1).Range.End
    selectedRange.Select
    Selection.Cells.Merge
    Selection.Orientation = wdTextOrientationUpward
    Selection.ParagraphFormat.Alignment = wdAlignRowCenter
    
    table.Borders.Enable = False
    
    Set selectedRange = table.Cell(1, 2).Range
    selectedRange.End = table.Cell(count - 1, count).Range.End
    selectedRange.Select
    Selection.Borders.Enable = True
    Selection.Borders.InsideColor = wdColorWhite
    Selection.Borders.OutsideColor = wdColorWhite
    Selection.Font.Bold = False
    table.Cell(count - 1, 2).Select
    Selection.Cells.Borders(wdBorderLeft).Color = table.Cell(1, 1).Shading.BackgroundPatternColor
    Selection.Cells.Borders(wdBorderBottom).Color = Selection.Cells.Borders(wdBorderLeft).Color
    Selection.Shading.BackgroundPatternColor = Selection.Cells.Borders(wdBorderLeft).Color
End Sub


Private Sub UpdateMeasureTable()
'
' UpdateGraphics Macro
' Applique le style Normal et convertit les titres sélectionnés en corps de texte.
'
    Dim table As table
    Dim selectedRange As Range
    Dim mergeRange As Range
    Dim columnWidth As Variant
    Dim columnsFixedWidth As Variant
    Dim columnsPreferenceWidth As Variant
    Dim style As String
    Dim row As row
    Dim rowCount As Integer
    columnWidth = Array(1, 3, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.3, 0.98, 8.3, 8.3)
    columnsFixedWidth = Array(2, 14, 15, 16)
    columnsPreferenceWidth = Array(1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    rowCount = UBound(columnWidth) + 1
    On Error GoTo UnknownError
        'ActiveWindow.WindowState = xlMinimized
        For Each table In ActiveDocument.Tables
            If StartsWith(table.style, "TableTS") Then
                style = table.style
                ReplaceTableFormat table, "^l", "^p"
                ReplaceTableFormat table, "^p^p", "^p"
                If StrComp(style, "TableTSRiskHeatMap") = 0 Then
                    formatRiskHeatMap table
                Else
                    Set selectedRange = table.Rows(1).Range
                    selectedRange.Select
                    selectedRange.Rows.HeadingFormat = True
                End If
                
                If StrComp(style, "TableTSProba") = 0 Or StrComp(style, "TableTSImpact") = 0 Then
                    table.AutoFitBehavior wdAutoFitContent
                    Selection.Collapse
                ElseIf StrComp(style, "TableTSMeasure") = 0 Then
                    table.Select
                    Selection.Font.size = 8
                    For Each i In columnsFixedWidth
                        table.Columns(i).Width = CentimetersToPoints(columnWidth(i - 1))
                    Next i
                    For Each i In columnsPreferenceWidth
                       table.Columns(i).PreferredWidthType = wdPreferredWidthPoints
                       table.Columns(i).PreferredWidth = CentimetersToPoints(columnWidth(i - 1))
                    Next i
                    For Each row In table.Rows
                        If row.Cells(3).Range.Text = Chr(13) & Chr(7) Then
                          Set mergeRange = row.Cells(2).Range
                          mergeRange.End = row.Cells(rowCount - 1).Range.End
                          mergeRange.Select
                          Selection.Cells.Merge
                        End If
                    Next row
                    table.PreferredWidthType = wdPreferredWidthPoints
                    table.PreferredWidth = CentimetersToPoints(28.5)
                Else:
                    table.AutoFitBehavior wdAutoFitWindow
                    table.AutoFitBehavior wdAutoFitContent
                    If StrComp(style, "TableTSSummary") = 0 Then
                        For Each RowIndex In Array(table.Rows.count, table.Rows.count - 1, table.Rows.count - 6)
                            table.Rows(RowIndex).Select
                            Selection.Font.Bold = True
                        Next RowIndex
                    ElseIf StrComp(style, "TableTSAssessment") = 0 Then
                        table.Select
                        Selection.MoveDown Unit:=wdLine, count:=1
                        Selection.InsertCaption Label:="Table", TitleAutoText:="InsertCaption", _
                        Title:="", Position:=wdCaptionPositionBelow, ExcludeLabel:=0
                        Selection.TypeText Text:=": "
                    End If
                End If
                table.Rows.Alignment = wdAlignRowCenter
            End If
        Next table
        'ActiveWindow.WindowState = wdWindowStateNormal
    Exit Sub
UnknownError:
       MsgBox "An unknow error occurred while formatting content"
       'ActiveWindow.WindowState = wdWindowStateNormal
End Sub