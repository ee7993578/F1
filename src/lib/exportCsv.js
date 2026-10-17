function cell(value) {
  const text = value == null ? '' : String(value)
  return '"' + text.replace(/"/g, '""') + '"'
}

export function downloadCsv(filename, rows) {
  if (!Array.isArray(rows) || rows.length === 0) return false
  const columns = Array.from(new Set(rows.flatMap(row => Object.keys(row || {}))))
  const csv = [columns, ...rows.map(row => columns.map(column => row?.[column]))]
    .map(row => row.map(cell).join(','))
    .join('\r\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
  return true
}
