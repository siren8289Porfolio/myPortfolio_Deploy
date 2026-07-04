import { useState } from "react";
import { createVerification, uploadFile, getContext } from "./api/client.js";

const INPUT_TYPES = [
  { value: "PASTE", label: "코드 붙여넣기" },
  { value: "FILE", label: "파일 업로드" },
  { value: "COMMIT", label: "커밋" },
  { value: "PR", label: "Pull Request" },
];

const STEP = {
  INPUT: "INPUT",
  UPLOAD: "UPLOAD",
  CONTEXT: "CONTEXT",
};

function EmptyField({ label, value, onChange, placeholder, textarea }) {
  const Tag = textarea ? "textarea" : "input";
  return (
    <label className="field">
      <span>{label}</span>
      <Tag
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        rows={textarea ? 8 : undefined}
      />
    </label>
  );
}

export default function App() {
  const [step, setStep] = useState(STEP.INPUT);
  const [inputType, setInputType] = useState("PASTE");
  const [rawInput, setRawInput] = useState("");
  const [repoUrl, setRepoUrl] = useState("");
  const [commitHash, setCommitHash] = useState("");
  const [prNumber, setPrNumber] = useState("");
  const [file, setFile] = useState(null);
  const [verification, setVerification] = useState(null);
  const [context, setContext] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function reset() {
    setStep(STEP.INPUT);
    setInputType("PASTE");
    setRawInput("");
    setRepoUrl("");
    setCommitHash("");
    setPrNumber("");
    setFile(null);
    setVerification(null);
    setContext(null);
    setError("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = {
        inputType,
        inputMethod: inputType === "FILE" ? "FILE_UPLOAD" : "TEXTAREA",
        rawInput: inputType === "PASTE" ? rawInput : undefined,
        repoUrl: inputType === "COMMIT" || inputType === "PR" ? repoUrl : undefined,
        commitHash: inputType === "COMMIT" ? commitHash : undefined,
        prNumber: inputType === "PR" ? Number(prNumber) : undefined,
      };
      const created = await createVerification(payload);
      setVerification(created);

      if (created.nextAction === "UPLOAD_FILE") {
        setStep(STEP.UPLOAD);
      } else {
        const ctx = await getContext(created.id);
        setContext(ctx);
        setStep(STEP.CONTEXT);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleUpload(e) {
    e.preventDefault();
    if (!file || !verification) return;
    setError("");
    setLoading(true);
    try {
      await uploadFile(verification.id, file);
      const ctx = await getContext(verification.id);
      setContext(ctx);
      setStep(STEP.CONTEXT);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <header className="header">
        <h1>MIDO</h1>
        <p>AI 결과, 우리 팀 기준에 맞는지 판단 전에 맥락부터 확인하세요.</p>
      </header>

      <ol className="steps">
        <li className={step === STEP.INPUT ? "active" : ""}>1. 입력</li>
        <li className={step === STEP.UPLOAD ? "active" : ""}>2. 파일 업로드</li>
        <li className={step === STEP.CONTEXT ? "active" : ""}>3. 컨텍스트 확인</li>
      </ol>

      {error && <div className="error">{error}</div>}

      {step === STEP.INPUT && (
        <form className="card" onSubmit={handleSubmit}>
          <label className="field">
            <span>입력 방식</span>
            <select value={inputType} onChange={(e) => setInputType(e.target.value)}>
              {INPUT_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
          </label>

          {inputType === "PASTE" && (
            <EmptyField
              label="검토할 코드"
              value={rawInput}
              onChange={setRawInput}
              placeholder="여기에 코드를 붙여넣으세요"
              textarea
            />
          )}

          {inputType === "FILE" && (
            <p className="hint">다음 단계에서 파일을 업로드합니다.</p>
          )}

          {(inputType === "COMMIT" || inputType === "PR") && (
            <EmptyField
              label="Git 저장소 URL"
              value={repoUrl}
              onChange={setRepoUrl}
              placeholder="https://github.com/org/repo"
            />
          )}
          {inputType === "COMMIT" && (
            <EmptyField
              label="커밋 해시"
              value={commitHash}
              onChange={setCommitHash}
              placeholder="a1b2c3d"
            />
          )}
          {inputType === "PR" && (
            <EmptyField
              label="PR 번호"
              value={prNumber}
              onChange={setPrNumber}
              placeholder="42"
            />
          )}

          <button type="submit" disabled={loading}>
            {loading ? "제출 중..." : "판단 세션 생성"}
          </button>
        </form>
      )}

      {step === STEP.UPLOAD && verification && (
        <form className="card" onSubmit={handleUpload}>
          <p className="hint">
            판단 세션이 생성되었습니다 (ID: {verification.id}). 검토할 파일을 업로드하세요.
          </p>
          <label className="field">
            <span>파일</span>
            <input type="file" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
          </label>
          <button type="submit" disabled={loading || !file}>
            {loading ? "업로드 중..." : "업로드"}
          </button>
        </form>
      )}

      {step === STEP.CONTEXT && (
        <div className="card">
          <h2>작업 컨텍스트</h2>
          {verification && (
            <p className="hint">
              세션 ID: {verification.id} · 상태: {verification.status}
            </p>
          )}
          {context && (
            <dl className="context-list">
              {context.repoUrl && (
                <>
                  <dt>저장소</dt>
                  <dd>{context.repoUrl}</dd>
                </>
              )}
              {context.commitHash && (
                <>
                  <dt>커밋</dt>
                  <dd>{context.commitHash}</dd>
                </>
              )}
              {context.prNumber != null && (
                <>
                  <dt>PR 번호</dt>
                  <dd>{context.prNumber}</dd>
                </>
              )}
              {context.fileName && (
                <>
                  <dt>파일명</dt>
                  <dd>{context.fileName}</dd>
                </>
              )}
              {context.snippet && (
                <>
                  <dt>코드 스니펫</dt>
                  <dd>
                    <pre>{context.snippet}</pre>
                  </dd>
                </>
              )}
            </dl>
          )}
          <p className="hint">
            판단(Use/Fix/Ignore)·가이드라인 기능은 다음 버전(MVP-2)에서 제공될 예정입니다.
          </p>
          <button type="button" onClick={reset}>
            새로 시작
          </button>
        </div>
      )}
    </div>
  );
}
