param(
    [Parameter(Mandatory=$true, Position=0)]
    [string]$TodoPath
)

$content = Get-Content -Raw -LiteralPath $TodoPath

# Replace any pick line whose subject matches the offending message
# Example line: "pick 3055b79 chore: Add .claude/ to .gitignore"
$lines = $content -split "`n"
$updated = $lines | ForEach-Object {
    if ($_ -match '^[\t ]*pick[\t ]+([0-9a-f]{7,40})[\t ]+.*Add \.claude/ to \.gitignore') {
        $_ -replace '^[\t ]*pick', 'reword'
    } else { $_ }
}

$out = [string]::Join("`n", $updated)
Set-Content -LiteralPath $TodoPath -Value $out -NoNewline
exit 0
