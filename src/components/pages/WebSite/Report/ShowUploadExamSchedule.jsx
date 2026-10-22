/**
 * ShowUploadExamSchedule.jsx
 * Converts legacy ASPX "Show Uploaded Exam Schedule" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Class & Exam dropdowns with validation
 *  - Submit to fetch schedule records
 *  - Desktop: ERP-style data-dense table
 *  - Mobile: Card-based layout with file viewer modal
 *  - File preview modal (PDF/image viewer simulation)
 *  - Loading states, empty states, toast notifications
 */

import { useState, useMemo, useCallback } from "react";
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check,
  Loader2, ChevronDown, FileText, Download, Search,
  BookOpen, Calendar, GraduationCap, SlidersHorizontal,
  FileSpreadsheet, File, ExternalLink, ClipboardList,
  ChevronRight, Info, School
} from "lucide-react";

// ─── STATIC / DUMMY DATA ─────────────────────────────────────────────────────

const CLASSES = [
  { value: "0", label: "-- Select Class --" },
  { value: "nursery", label: "Nursery" },
  { value: "lkg", label: "LKG" },
  { value: "ukg", label: "UKG" },
  { value: "class1", label: "Class I" },
  { value: "class2", label: "Class II" },
  { value: "class3", label: "Class III" },
  { value: "class4", label: "Class IV" },
  { value: "class5", label: "Class V" },
  { value: "class6", label: "Class VI" },
  { value: "class7", label: "Class VII" },
  { value: "class8", label: "Class VIII" },
  { value: "class9", label: "Class IX" },
  { value: "class10", label: "Class X" },
  { value: "class11", label: "Class XI" },
  { value: "class12", label: "Class XII" },
];

const EXAMS_BY_CLASS = {
  nursery: ["Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  lkg: ["Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  ukg: ["Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  class1: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class2: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class3: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  class4: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  class5: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2"],
  class6: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class7: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class8: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class9: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board", "Board Prep"],
  class10: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board", "Board Prep"],
  class11: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board"],
  class12: ["Half Yearly 2024-25", "Annual Exam 2024-25", "Unit Test 1", "Unit Test 2", "Pre-Board", "Board Exam"],
};

// Simulated schedule data per class+exam
const SCHEDULE_DATA = {
  "class9-Half Yearly 2024-25": [
    { id: 1, title: "Class IX Half Yearly Schedule 2024-25 (Science Stream)", upload_file: "class9_hy_science_2024.pdf", type: "pdf", uploadedOn: "12 Oct 2024" },
    { id: 2, title: "Class IX Half Yearly Schedule 2024-25 (Commerce Stream)", upload_file: "class9_hy_commerce_2024.pdf", type: "pdf", uploadedOn: "12 Oct 2024" },
    { id: 3, title: "Class IX Half Yearly Practical Schedule", upload_file: "class9_hy_practical.pdf", type: "pdf", uploadedOn: "13 Oct 2024" },
  ],
  "class10-Annual Exam 2024-25": [
    { id: 1, title: "Class X Annual Examination Schedule 2024-25", upload_file: "class10_annual_2025.pdf", type: "pdf", uploadedOn: "05 Feb 2025" },
    { id: 2, title: "Class X Board Practical Timetable", upload_file: "class10_board_practical.pdf", type: "pdf", uploadedOn: "06 Feb 2025" },
  ],
  "class12-Pre-Board": [
    { id: 1, title: "Class XII Pre-Board Exam Schedule (Science)", upload_file: "class12_preboard_sci.pdf", type: "pdf", uploadedOn: "18 Jan 2025" },
    { id: 2, title: "Class XII Pre-Board Exam Schedule (Commerce)", upload_file: "class12_preboard_com.pdf", type: "pdf", uploadedOn: "18 Jan 2025" },
    { id: 3, title: "Class XII Pre-Board Exam Schedule (Arts)", upload_file: "class12_preboard_arts.pdf", type: "pdf", uploadedOn: "19 Jan 2025" },
  ],
  "class6-Half Yearly 2024-25": [
    { id: 1, title: "Class VI Half Yearly Exam Timetable 2024-25", upload_file: "class6_hy_2024.pdf", type: "pdf", uploadedOn: "10 Oct 2024" },
  ],
  "class8-Unit Test 1": [
    { id: 1, title: "Class VIII Unit Test 1 Schedule", upload_file: "class8_ut1.pdf", type: "pdf", uploadedOn: "22 Jul 2024" },
    { id: 2, title: "Class VIII UT-1 Syllabus Sheet", upload_file: "class8_ut1_syllabus.pdf", type: "pdf", uploadedOn: "22 Jul 2024" },
  ],
};

// Helper to get schedule records
const getSchedule = (classVal, exam) => {
  const key = `${classVal}-${exam}`;
  if (SCHEDULE_DATA[key]) return SCHEDULE_DATA[key];
  // Generate generic data for other combinations
  if (classVal && classVal !== "0" && exam && exam !== "0") {
    const cls = CLASSES.find(c => c.value === classVal)?.label || classVal;
    return [
      { id: 1, title: `${cls} ${exam} Timetable 2024-25`, upload_file: `${classVal}_${exam.toLowerCase().replace(/\s+/g, '_')}.pdf`, type: "pdf", uploadedOn: "15 Aug 2024" },
      { id: 2, title: `${cls} ${exam} Syllabus & Guidelines`, upload_file: `${classVal}_${exam.toLowerCase().replace(/\s+/g, '_')}_syllabus.pdf`, type: "pdf", uploadedOn: "15 Aug 2024" },
    ];
  }
  return [];
};

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, error, disabled }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2.5 text-[13px] rounded-xl border outline-none transition-all cursor-pointer
          bg-white text-slate-800
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error
            ? "border-rose-400 ring-2 ring-rose-100"
            : "border-slate-200 dark:border-[rgba(99,102,241,0.25)]"
          }`}
      >
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  );
}

function Field({ label, error, required, children }) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
        {label}
        {required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500 mt-0.5">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />
          {error}
        </p>
      )}
    </div>
  );
}

function Toast({ message, type = "success", onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-[999] flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === "success" ? "bg-emerald-600 text-white" : "bg-rose-600 text-white"}`}
      style={{ animation: "slideUp .25s ease" }}
    >
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(12px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
      {type === "success"
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
    </div>
  );
}

// ─── FILE TYPE BADGE ──────────────────────────────────────────────────────────
function FileTypeBadge({ type = "pdf" }) {
  const config = {
    pdf: { bg: "bg-rose-50 text-rose-600 border-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:border-rose-500/20", icon: FileText },
    xlsx: { bg: "bg-emerald-50 text-emerald-600 border-emerald-100 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20", icon: FileSpreadsheet },
    doc: { bg: "bg-blue-50 text-blue-600 border-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20", icon: File },
  };
  const { bg, icon: Icon } = config[type] || config.pdf;
  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold uppercase tracking-wide border ${bg}`}>
      <Icon className="w-3 h-3" />
      {type.toUpperCase()}
    </span>
  );
}

// ─── FILE PREVIEW MODAL ───────────────────────────────────────────────────────
function FilePreviewModal({ file, onClose }) {
  if (!file) return null;
  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 z-40 backdrop-blur-sm"
        onClick={onClose}
      />
      {/* Modal */}
      <div
        className="fixed z-50 inset-4 sm:inset-10 md:inset-16 lg:inset-20 rounded-2xl bg-white dark:bg-[#1a1f35]
          border border-slate-200 dark:border-[rgba(99,102,241,0.25)] shadow-2xl flex flex-col overflow-hidden"
        style={{ animation: "modalIn .2s ease" }}
      >
        <style>{`@keyframes modalIn{from{opacity:0;transform:scale(.97)}to{opacity:1;transform:scale(1)}}`}</style>

        {/* Modal Header */}
        <div className="flex items-center gap-3 px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/80 dark:bg-white/[0.02] flex-shrink-0">
          <div className="w-8 h-8 rounded-lg bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center flex-shrink-0">
            <FileText className="w-4 h-4 text-rose-600 dark:text-rose-400" />
          </div>
          <div className="flex-1 min-w-0">
            <h3 className="text-[14px] font-bold text-slate-800 dark:text-slate-100 truncate">{file.title}</h3>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{file.upload_file}</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors flex-shrink-0"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body — Simulated file viewer */}
        <div className="flex-1 overflow-auto flex flex-col items-center justify-center bg-slate-100 dark:bg-[#131726] p-6 gap-6">
          {/* Simulated PDF placeholder */}
          <div className="w-full max-w-lg bg-white dark:bg-[#1a1f35] rounded-2xl shadow-lg border border-slate-200 dark:border-[rgba(99,102,241,0.2)] overflow-hidden">
            {/* Fake PDF toolbar */}
            <div className="flex items-center justify-between px-4 py-2.5 bg-slate-700 dark:bg-slate-900">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-rose-400" />
                <div className="w-3 h-3 rounded-full bg-amber-400" />
                <div className="w-3 h-3 rounded-full bg-emerald-400" />
              </div>
              <span className="text-[11px] text-slate-300 font-mono truncate max-w-[200px]">{file.upload_file}</span>
              <div className="w-16" />
            </div>

            {/* Fake document preview */}
            <div className="p-8 space-y-4">
              <div className="text-center space-y-2 mb-6">
                <div className="w-16 h-16 bg-rose-100 dark:bg-rose-500/20 rounded-2xl flex items-center justify-center mx-auto">
                  <FileText className="w-8 h-8 text-rose-600 dark:text-rose-400" />
                </div>
                <h4 className="text-[15px] font-bold text-slate-800 dark:text-slate-100">{file.title}</h4>
                <p className="text-[12px] text-slate-400">Uploaded on {file.uploadedOn}</p>
              </div>

              {/* Simulated content lines */}
              {[...Array(8)].map((_, i) => (
                <div
                  key={i}
                  className="h-3 rounded-full bg-slate-100 dark:bg-slate-800"
                  style={{ width: `${65 + Math.sin(i * 1.7) * 25}%` }}
                />
              ))}
              <div className="mt-4 grid grid-cols-3 gap-3">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="h-16 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-700" />
                ))}
              </div>
              {[...Array(4)].map((_, i) => (
                <div
                  key={i}
                  className="h-2.5 rounded-full bg-slate-100 dark:bg-slate-800"
                  style={{ width: `${50 + Math.cos(i * 2.1) * 30}%` }}
                />
              ))}
            </div>
          </div>

          <p className="text-[12px] text-slate-400 dark:text-slate-500 text-center">
            File preview simulation — connect to actual file server for real preview.
          </p>
        </div>

        {/* Modal Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/80 dark:bg-white/[0.02] flex-shrink-0">
          <FileTypeBadge type={file.type} />
          <div className="flex items-center gap-2">
            <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-blue-600 text-white hover:bg-blue-700 shadow-sm transition-all active:scale-95">
              <Download className="w-4 h-4" />
              Download
            </button>
            <button
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </>
  );
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, onView }) {
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-blue-50/30 dark:hover:bg-white/[0.02] transition-colors group">
      {/* S.No */}
      <td className="px-5 py-3.5 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-14 font-mono">
        {idx}
      </td>

      {/* Title */}
      <td className="px-5 py-3.5">
        <div className="flex items-start gap-3">
          <div className="w-8 h-8 rounded-lg bg-rose-50 dark:bg-rose-500/10 flex items-center justify-center flex-shrink-0 mt-0.5">
            <FileText className="w-4 h-4 text-rose-500 dark:text-rose-400" />
          </div>
          <div className="min-w-0">
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 leading-snug">{row.title}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 font-mono">{row.upload_file}</p>
          </div>
        </div>
      </td>

      {/* Uploaded On */}
      <td className="px-5 py-3.5 text-center">
        <span className="text-[12px] text-slate-500 dark:text-slate-400">{row.uploadedOn}</span>
      </td>

      {/* Type */}
      <td className="px-5 py-3.5 text-center">
        <FileTypeBadge type={row.type} />
      </td>

      {/* Action */}
      <td className="px-5 py-3.5 text-center">
        <button
          onClick={() => onView(row)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200
            dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20 dark:hover:bg-blue-500/20
            transition-all group-hover:shadow-sm"
        >
          <Eye className="w-3.5 h-3.5" />
          View
        </button>
      </td>
    </tr>
  );
}

// ─── MOBILE SCHEDULE CARD ─────────────────────────────────────────────────────
function MobileScheduleCard({ row, idx, onView }) {
  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      <div className="flex items-start gap-3 p-4">
        {/* Icon */}
        <div className="w-11 h-11 rounded-xl bg-rose-50 dark:bg-rose-500/10 border border-rose-100 dark:border-rose-500/20 flex items-center justify-center flex-shrink-0">
          <FileText className="w-5 h-5 text-rose-500 dark:text-rose-400" />
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 leading-snug pr-1">{row.title}</p>
            <span className="text-[11px] text-slate-400 dark:text-slate-500 flex-shrink-0 tabular-nums"># {idx}</span>
          </div>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 font-mono mt-1 truncate">{row.upload_file}</p>
          <div className="flex items-center gap-2 mt-2">
            <FileTypeBadge type={row.type} />
            <span className="text-[11px] text-slate-400 dark:text-slate-500">· {row.uploadedOn}</span>
          </div>
        </div>
      </div>

      {/* Action Bar */}
      <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex">
        <button
          onClick={() => onView(row)}
          className="flex-1 flex items-center justify-center gap-2 py-3 text-[13px] font-semibold
            text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-500/10 transition-colors"
        >
          <Eye className="w-4 h-4" />
          View File
        </button>
        <div className="w-px bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
        <button
          className="flex-1 flex items-center justify-center gap-2 py-3 text-[13px] font-semibold
            text-emerald-600 dark:text-emerald-400 hover:bg-emerald-50 dark:hover:bg-emerald-500/10 transition-colors"
        >
          <Download className="w-4 h-4" />
          Download
        </button>
      </div>
    </div>
  );
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, selectedClass, setSelectedClass, selectedExam, setSelectedExam, exams, onSubmit, loading, errors }) {
  if (!open) return null;
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35]
          border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ animation: "drawerUp .25s ease" }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>

        {/* Handle */}
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Search Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Fields */}
        <div className="px-5 py-5 space-y-4">
          <Field label="Select Class" error={errors.selectedClass} required>
            <NativeSelect
              value={selectedClass}
              onChange={e => setSelectedClass(e.target.value)}
              error={errors.selectedClass}
            >
              {CLASSES.map(c => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </NativeSelect>
          </Field>
          <Field label="Select Exam" error={errors.selectedExam} required>
            <NativeSelect
              value={selectedExam}
              onChange={e => setSelectedExam(e.target.value)}
              error={errors.selectedExam}
              disabled={!selectedClass || selectedClass === "0"}
            >
              <option value="0">-- Select Exam --</option>
              {exams.map(e => (
                <option key={e} value={e}>{e}</option>
              ))}
            </NativeSelect>
          </Field>
        </div>

        {/* Footer Buttons */}
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold
              bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={() => { onSubmit(); onClose(); }}
            disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 disabled:opacity-70 transition-all"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
            Show Schedule
          </button>
        </div>
      </div>
    </>
  );
}

// ─── SUMMARY HEADER BANNER ────────────────────────────────────────────────────
function ResultBanner({ className, exam, count }) {
  return (
    <div className="rounded-2xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)]
      bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35]
      px-5 py-4 shadow-sm">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex items-center gap-3 flex-1 min-w-0">
          <div className="w-11 h-11 rounded-xl bg-blue-600 dark:bg-indigo-600 flex items-center justify-center flex-shrink-0 shadow-md shadow-blue-500/20">
            <Calendar className="w-5 h-5 text-white" />
          </div>
          <div className="min-w-0">
            <h3 className="text-[15px] font-bold text-slate-800 dark:text-slate-100 leading-tight">
              Exam Schedule — {className}
            </h3>
            <p className="text-[12px] text-slate-500 dark:text-slate-400 mt-0.5 truncate">{exam}</p>
          </div>
        </div>
        <div className="flex items-center gap-3 flex-shrink-0">
          <div className="text-center px-4 py-2 rounded-xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-sm">
            <p className="text-[20px] font-bold text-blue-700 dark:text-blue-400 tabular-nums">{count}</p>
            <p className="text-[10px] font-semibold text-slate-400 uppercase tracking-wide">
              {count === 1 ? "File" : "Files"}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export { ShowUploadExamSchedule as default } from '../WebsiteModule'
function LegacyShowUploadExamSchedule() {
  const [selectedClass, setSelectedClass] = useState("0");
  const [selectedExam, setSelectedExam]   = useState("0");
  const [rows, setRows]                   = useState([]);
  const [loading, setLoading]             = useState(false);
  const [filterOpen, setFilterOpen]       = useState(false);
  const [errors, setErrors]               = useState({});
  const [toast, setToast]                 = useState(null);
  const [shown, setShown]                 = useState(false);
  const [shownClass, setShownClass]       = useState("");
  const [shownExam, setShownExam]         = useState("");
  const [previewFile, setPreviewFile]     = useState(null);
  const [search, setSearch]               = useState("");

  // Derived exam list from selected class
  const exams = useMemo(() => {
    if (!selectedClass || selectedClass === "0") return [];
    return EXAMS_BY_CLASS[selectedClass] || [];
  }, [selectedClass]);

  // When class changes, reset exam
  const handleClassChange = (val) => {
    setSelectedClass(val);
    setSelectedExam("0");
    setErrors(p => ({ ...p, selectedClass: undefined }));
  };

  const showToast = (msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3500);
  };

  // ── Submit / Fetch ─────────────────────────────────────────────────────────
  const handleSubmit = useCallback(() => {
    const err = {};
    if (!selectedClass || selectedClass === "0") err.selectedClass = "Please select a class";
    if (!selectedExam || selectedExam === "0") err.selectedExam = "Please select an exam";
    if (Object.keys(err).length) { setErrors(err); return; }
    setErrors({});
    setLoading(true);
    setSearch("");

    setTimeout(() => {
      const data = getSchedule(selectedClass, selectedExam);
      setRows(data);
      const className = CLASSES.find(c => c.value === selectedClass)?.label || selectedClass;
      setShownClass(className);
      setShownExam(selectedExam);
      setShown(true);
      setLoading(false);
      if (data.length === 0) {
        showToast("No schedule files found for this selection.", "error");
      } else {
        showToast(`Found ${data.length} schedule file${data.length > 1 ? "s" : ""}.`);
      }
    }, 700);
  }, [selectedClass, selectedExam]);

  const handleReset = () => {
    setSelectedClass("0");
    setSelectedExam("0");
    setRows([]);
    setErrors({});
    setShown(false);
    setShownClass("");
    setShownExam("");
    setSearch("");
  };

  // ── Search filter ──────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search.trim()) return rows;
    const q = search.toLowerCase();
    return rows.filter(r =>
      r.title.toLowerCase().includes(q) ||
      r.upload_file.toLowerCase().includes(q)
    );
  }, [rows, search]);

  const hasResults = shown && rows.length > 0;
  const activeFilters = [selectedClass !== "0", selectedExam !== "0"].filter(Boolean).length;

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0f1221] p-4 sm:p-6 lg:p-8">
      <div className="max-w-5xl mx-auto space-y-5">

        {/* ── Page Header ─────────────────────────────────────────────────── */}
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
          <div>
            <nav className="flex items-center gap-1.5 text-[11px] text-slate-400 dark:text-slate-500 mb-2">
              <School className="w-3.5 h-3.5" />
              <span>Home</span>
              <ChevronRight className="w-3 h-3" />
              <span className="text-slate-600 dark:text-slate-300 font-semibold">Uploaded Exam Schedule</span>
            </nav>
            <h1 className="text-[20px] sm:text-[22px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-xl bg-blue-600 dark:bg-indigo-600 flex items-center justify-center shadow-md shadow-blue-500/20">
                <ClipboardList className="w-4 h-4 text-white" />
              </div>
              Uploaded Exam Schedule
            </h1>
            <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-1">
              View and download exam timetables uploaded by administration.
            </p>
          </div>
        </div>

        {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
        <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
          bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

          {/* Card Header */}
          <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)]
            bg-slate-50/70 dark:bg-white/[0.02]">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
          </div>

          {/* Filter Fields */}
          <div className="p-5">
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">

              {/* Class */}
              <Field label="Select Class" error={errors.selectedClass} required>
                <NativeSelect
                  value={selectedClass}
                  onChange={e => handleClassChange(e.target.value)}
                  error={errors.selectedClass}
                >
                  {CLASSES.map(c => (
                    <option key={c.value} value={c.value}>{c.label}</option>
                  ))}
                </NativeSelect>
              </Field>

              {/* Exam */}
              <Field label="Select Exam" error={errors.selectedExam} required>
                <NativeSelect
                  value={selectedExam}
                  onChange={e => { setSelectedExam(e.target.value); setErrors(p => ({ ...p, selectedExam: undefined })); }}
                  error={errors.selectedExam}
                  disabled={!selectedClass || selectedClass === "0"}
                >
                  <option value="0">-- Select Exam --</option>
                  {exams.map(e => (
                    <option key={e} value={e}>{e}</option>
                  ))}
                </NativeSelect>
              </Field>

              {/* Spacer */}
              <div />

              {/* Buttons */}
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={handleSubmit}
                  disabled={loading}
                  className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                    bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                    transition-all active:scale-95 disabled:opacity-70"
                >
                  {loading
                    ? <Loader2 className="w-4 h-4 animate-spin" />
                    : <Eye className="w-4 h-4" />}
                  Submit
                </button>
                <button
                  type="button"
                  onClick={handleReset}
                  title="Reset filters"
                  className="flex items-center justify-center px-3 py-2.5 rounded-xl text-[13px] font-semibold
                    bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* ── MOBILE Filter Bar ─────────────────────────────────────────────── */}
        <div className="flex sm:hidden gap-2">
          <button
            type="button"
            onClick={() => setFilterOpen(true)}
            className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl text-[13px] font-semibold
              bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20"
          >
            <SlidersHorizontal className="w-4 h-4" />
            {activeFilters > 0
              ? `${activeFilters} Filter${activeFilters > 1 ? "s" : ""} Applied`
              : "Select Class & Exam"}
            {activeFilters > 0 && (
              <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">
                {activeFilters}
              </span>
            )}
          </button>
          {(selectedClass !== "0" || selectedExam !== "0") && (
            <button
              type="button"
              onClick={handleReset}
              className="px-3.5 py-3 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300"
            >
              <RefreshCw className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Mobile Filter Drawer */}
        <FilterDrawer
          open={filterOpen}
          onClose={() => setFilterOpen(false)}
          selectedClass={selectedClass}
          setSelectedClass={handleClassChange}
          selectedExam={selectedExam}
          setSelectedExam={setSelectedExam}
          exams={exams}
          onSubmit={handleSubmit}
          loading={loading}
          errors={errors}
        />

        {/* ── Loading Skeleton ─────────────────────────────────────────────── */}
        {loading && (
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
            bg-white dark:bg-[#1a1f35] p-6 space-y-3">
            <div className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-5" />
            {[...Array(3)].map((_, i) => (
              <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse"
                style={{ opacity: 1 - i * 0.18 }} />
            ))}
          </div>
        )}

        {/* ── Results Section ──────────────────────────────────────────────── */}
        {hasResults && !loading && (
          <>
            {/* Result Banner */}
            <ResultBanner className={shownClass} exam={shownExam} count={rows.length} />

            {/* Data Card */}
            <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
              bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

              {/* Card Header */}
              <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5
                border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)]
                bg-slate-50/70 dark:bg-white/[0.02]">
                <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                  <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                  <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                  <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Schedule Files</span>
                  <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full
                    bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                    {filtered.length} file{filtered.length !== 1 ? "s" : ""}
                  </span>
                </div>

                {/* Search */}
                <div className="relative w-full sm:w-52 flex-shrink-0">
                  <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <input
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    placeholder="Search files…"
                    className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                      bg-white text-slate-700 border-slate-200 placeholder-slate-300
                      focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                      dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                      dark:placeholder-slate-600 dark:focus:border-indigo-400"
                  />
                  {search && (
                    <button
                      onClick={() => setSearch("")}
                      className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    >
                      <X className="w-3.5 h-3.5" />
                    </button>
                  )}
                </div>
              </div>

              {/* Info hint */}
              <div className="flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)]
                bg-amber-50/30 dark:bg-amber-500/[0.03]">
                <Info className="w-3.5 h-3.5 text-amber-500 flex-shrink-0" />
                <p className="text-[12px] text-amber-700 dark:text-amber-400">
                  Click <strong>View</strong> to preview or <strong>Download</strong> to save the file locally.
                </p>
              </div>

              {/* ── DESKTOP TABLE ── */}
              <div className="hidden md:block overflow-x-auto">
                {filtered.length === 0 ? (
                  <div className="flex flex-col items-center justify-center h-28 gap-2 text-slate-400 dark:text-slate-600">
                    <Search className="w-6 h-6 opacity-40" />
                    <span className="text-[13px]">No files match your search.</span>
                  </div>
                ) : (
                  <table className="w-full">
                    <thead>
                      <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]
                        bg-slate-50/50 dark:bg-white/[0.02]">
                        {["S.No.", "Title / File Name", "Uploaded On", "Type", "Action"].map((h, i) => (
                          <th key={i}
                            className={`px-5 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                              ${i === 0 || i === 2 || i === 3 || i === 4 ? "text-center" : "text-left"} first:w-14`}
                          >
                            {h}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {filtered.map((row, i) => (
                        <DesktopRow
                          key={row.id}
                          row={row}
                          idx={i + 1}
                          onView={setPreviewFile}
                        />
                      ))}
                    </tbody>
                  </table>
                )}
              </div>

              {/* ── MOBILE CARDS ── */}
              <div className="md:hidden p-4 space-y-3">
                {filtered.length === 0 ? (
                  <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                    <Search className="w-6 h-6 opacity-40" />
                    <span className="text-[13px]">No files match your search.</span>
                  </div>
                ) : (
                  filtered.map((row, i) => (
                    <MobileScheduleCard
                      key={row.id}
                      row={row}
                      idx={i + 1}
                      onView={setPreviewFile}
                    />
                  ))
                )}
              </div>

              {/* Table Footer */}
              <div className="flex items-center justify-between gap-3 px-5 py-3.5
                border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)]
                bg-slate-50/50 dark:bg-white/[0.015]">
                <p className="text-[12px] text-slate-400 dark:text-slate-500">
                  Showing{" "}
                  <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span>
                  {" "}of{" "}
                  <span className="font-semibold text-slate-700 dark:text-slate-300">{rows.length}</span>
                  {" "}files
                </p>
                {search && (
                  <button
                    onClick={() => setSearch("")}
                    className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                  >
                    <X className="w-3 h-3" />
                    Clear search
                  </button>
                )}
              </div>
            </div>
          </>
        )}

        {/* ── No Data State (after submit, no results) ──────────────────────── */}
        {shown && rows.length === 0 && !loading && (
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
            bg-white dark:bg-[#1a1f35] shadow-sm p-12 flex flex-col items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-amber-50 dark:bg-amber-500/10 flex items-center justify-center">
              <FileText className="w-7 h-7 text-amber-400 dark:text-amber-500 opacity-60" />
            </div>
            <div className="text-center">
              <p className="text-[14px] font-bold text-slate-600 dark:text-slate-400">No Files Found</p>
              <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
                No schedule has been uploaded for <strong>{shownClass}</strong> — <strong>{shownExam}</strong>.
              </p>
            </div>
            <button
              onClick={handleReset}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-blue-600 text-white hover:bg-blue-700 shadow-sm transition-all"
            >
              <RefreshCw className="w-3.5 h-3.5" />
              Try Different Selection
            </button>
          </div>
        )}

        {/* ── Initial Empty State ──────────────────────────────────────────── */}
        {!shown && !loading && (
          <div className="flex flex-col items-center justify-center py-16 gap-5 text-slate-400 dark:text-slate-600">
            <div className="relative">
              <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <GraduationCap className="w-8 h-8 opacity-40" />
              </div>
              <div className="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-blue-500 flex items-center justify-center">
                <Search className="w-3.5 h-3.5 text-white" />
              </div>
            </div>
            <div className="text-center max-w-xs">
              <p className="text-[15px] font-bold text-slate-500 dark:text-slate-400">No report generated yet</p>
              <p className="text-[13px] text-slate-400 dark:text-slate-500 mt-1.5 leading-relaxed">
                Select a <strong>Class</strong> and <strong>Exam</strong> above, then click{" "}
                <strong>Submit</strong> to view uploaded exam schedules.
              </p>
            </div>

            {/* Quick tip */}
            <div className="flex items-start gap-2.5 max-w-sm px-4 py-3 rounded-xl
              bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20">
              <Info className="w-4 h-4 text-blue-500 flex-shrink-0 mt-0.5" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400 leading-relaxed">
                <strong>Tip:</strong> Exam options load automatically after you select a class. Try{" "}
                <strong>Class IX → Half Yearly 2024-25</strong> or{" "}
                <strong>Class XII → Pre-Board</strong>.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* ── File Preview Modal ─────────────────────────────────────────────── */}
      <FilePreviewModal
        file={previewFile}
        onClose={() => setPreviewFile(null)}
      />

      {/* ── Toast ─────────────────────────────────────────────────────────── */}
      {toast && (
        <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />
      )}
    </div>
  );
}
